package com.rms.restaurant_management_system;

import com.rms.restaurant_management_system.auth.service.JwtTokenService;
import com.rms.restaurant_management_system.config.SecurityConfig;
import com.rms.restaurant_management_system.menu_item.service.MenuService;
import com.rms.restaurant_management_system.product.service.ProductService;
import com.rms.restaurant_management_system.recipe_item.service.RecipeItemService;
import com.rms.restaurant_management_system.reservation.service.ReservationService;
import com.rms.restaurant_management_system.restaurant_table.service.RestaurantTableService;
import com.rms.restaurant_management_system.supply.service.SupplyService;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import com.rms.restaurant_management_system.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@ActiveProfiles("restaurant")
class AccessTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private RestaurantTableService restaurantTableService;

    @MockitoBean
    private RecipeItemService recipeItemService;

    @MockitoBean
    private SupplyService supplyService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithAnonymousUser
    void anonymousUserCannotAccessProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void authenticatedUserWithoutRequiredRoleCannotAccessProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CHEF")
    void chefCanAccessProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void waiterCannotAccessProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void waiterCanAccessMenuItems() throws Exception {
        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CHEF")
    void chefCanAccessMenuItems() throws Exception {
        mockMvc.perform(get("/api/menu-items"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CHEF")
    void chefCannotDeleteProducts() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanDeleteProducts() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void waiterCannotAccessUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCanAccessUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void restaurantServiceDoesNotExposeOrderEndpoints() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isNotFound());
    }
}
