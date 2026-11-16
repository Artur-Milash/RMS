package com.rms.restaurant_management_system;

import com.rms.restaurant_management_system.config.SecurityConfig;
import com.rms.restaurant_management_system.bill.service.BillService;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import com.rms.restaurant_management_system.order.order_item.service.OrderItemService;
import com.rms.restaurant_management_system.order.service.OrderService;
import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.user.model.User;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest
@Import(SecurityConfig.class)
@ActiveProfiles("orders")
class OrdersProfileTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private BillService billService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllBatchesOrderItemLookups() throws Exception {
        Order firstOrder = order(11L);
        Order secondOrder = order(12L);
        PageRequest pageRequest = PageRequest.of(0, 100);
        when(orderService.getAll(pageRequest)).thenReturn(
                new PageImpl<>(List.of(firstOrder, secondOrder), pageRequest, 105));
        when(orderItemService.getOrderItemsByOrderIds(List.of(11L, 12L))).thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(header().string("X-Total-Count", "105"))
                .andExpect(header().string("X-Total-Pages", "2"))
                .andExpect(header().string("Link", containsString("rel=\"next\"")));

        verify(orderService).getAll(pageRequest);
        verify(orderItemService).getOrderItemsByOrderIds(List.of(11L, 12L));
        verify(orderItemService, never()).getOrderItems(anyLong());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllRejectsPageSizesAboveLimit() throws Exception {
        mockMvc.perform(get("/api/orders").queryParam("size", "201"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void ordersServiceDoesNotExposeRestaurantEndpoints() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isNotFound());
    }

    private Order order(Long id) {
        return Order.builder()
                .id(id)
                .table(RestaurantTable.builder().id(1L).number(4).build())
                .waiter(User.builder().id(2L).fullName("Waiter").build())
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.OPEN)
                .build();
    }
}
