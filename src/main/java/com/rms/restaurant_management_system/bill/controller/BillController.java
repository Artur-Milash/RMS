package com.rms.restaurant_management_system.bill.controller;

import com.rms.restaurant_management_system.bill.dto.BillCreateRequest;
import com.rms.restaurant_management_system.bill.dto.BillResponse;
import com.rms.restaurant_management_system.bill.model.Bill;
import com.rms.restaurant_management_system.bill.model.PaymentMethod;
import com.rms.restaurant_management_system.bill.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public List<BillResponse> getAll() {
        return billService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public BillResponse getByOrder(@PathVariable Long orderId) {
        return toResponse(billService.getBillByOrder(orderId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public ResponseEntity<BillResponse> createAndPay(@Valid @RequestBody BillCreateRequest request) {
        Bill bill = billService.createAndPayBill(request.orderId(), request.paymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(bill));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public BillResponse pay(@PathVariable Long id, @RequestParam PaymentMethod paymentMethod) {
        return toResponse(billService.payBill(id, paymentMethod));
    }

    private BillResponse toResponse(Bill bill) {
        return new BillResponse(bill.getId(), bill.getOrder().getId(), bill.getTotalAmount(),
                bill.getPaymentMethod(), bill.getPaymentTime(), bill.getStatus());
    }
}