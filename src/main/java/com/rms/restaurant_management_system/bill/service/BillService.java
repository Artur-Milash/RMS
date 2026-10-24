package com.rms.restaurant_management_system.bill.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.InvalidStateException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.bill.model.Bill;
import com.rms.restaurant_management_system.bill.model.BillStatus;
import com.rms.restaurant_management_system.bill.model.PaymentMethod;
import com.rms.restaurant_management_system.bill.repo.BillRepository;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.repo.OrderItemRepository;
import com.rms.restaurant_management_system.order.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

        @Transactional(readOnly = true)
        public List<Bill> getAll() {
                return billRepository.findAll();
        }

    @Transactional
    public Bill createBill(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new InvalidStateException("Order is not open");
        }

        if (billRepository.existsByOrderId(orderId)) {
            throw new BusinessConflictException("Bill already exists");
        }

        List<OrderItem> items =
                orderItemRepository.findByOrderId(orderId);

        BigDecimal total = items.stream()
                .map(item ->
                        item.getPriceAtOrder()
                                .multiply(
                                        BigDecimal.valueOf(item.getQuantity())
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Bill bill = Bill.builder()
                .order(order)
                .totalAmount(total)
                .status(BillStatus.PENDING)
                .build();

        return billRepository.save(bill);
    }

        @Transactional
        public Bill createAndPayBill(Long orderId, PaymentMethod paymentMethod) {
                Bill bill = createBill(orderId);
                return payBill(bill.getId(), paymentMethod);
        }

    @Transactional
    public Bill payBill(
            Long billId,
            PaymentMethod paymentMethod
    ) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        if (bill.getStatus() != BillStatus.PENDING) {
            throw new InvalidStateException("Bill is already paid");
        }

        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentTime(LocalDateTime.now());
        bill.setStatus(BillStatus.PAID);

        Order order = bill.getOrder();
        order.setStatus(OrderStatus.CLOSED);

        return bill;
    }

    @Transactional(readOnly = true)
    public Bill getBillByOrder(Long orderId) {
        return billRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
    }
}
