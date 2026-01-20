package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    public OrderService(CartRepository cartRepo,
                        ProductRepository productRepo,
                        OrderRepository orderRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    public Order createOrder(String userId) {
        List<CartItem> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cart : cartItems) {
            Product product = productRepo.findById(cart.getProductId()).orElseThrow();
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Stock not available");
            }

            product.setStock(product.getStock() - cart.getQuantity());
            productRepo.save(product);

            orderItems.add(new OrderItem(
                    product.getId(),
                    cart.getQuantity(),
                    product.getPrice()
            ));

            total += product.getPrice() * cart.getQuantity();
        }

        Order order = new Order(userId, total, "CREATED", orderItems);
        orderRepo.save(order);
        cartRepo.deleteByUserId(userId);

        return order;
    }

    public Order getOrder(String orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void markPaid(String orderId) {
        Order order = getOrder(orderId);
        order.setStatus("PAID");
        orderRepo.save(order);
    }
}
