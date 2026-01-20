package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order create(@RequestParam String userId) {
        return service.createOrder(userId);
    }

    @GetMapping("/{orderId}")
    public Order get(@PathVariable String orderId) {
        return service.getOrder(orderId);
    }
}
