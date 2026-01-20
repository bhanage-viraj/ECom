package com.example.ecommerce.controller;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public CartItem add(@RequestParam String userId,
                        @RequestParam String productId,
                        @RequestParam int quantity) {
        return service.addToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public List<CartItem> get(@PathVariable String userId) {
        return service.getCart(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clear(@PathVariable String userId) {
        service.clearCart(userId);
    }
}
