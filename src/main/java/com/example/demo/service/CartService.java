package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final ProductService productService;

    public CartService(CartRepository cartRepo, ProductService productService) {
        this.cartRepo = cartRepo;
        this.productService = productService;
    }

    public CartItem addToCart(String userId, String productId, int quantity) {
        Product product = productService.getById(productId);

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        CartItem item = new CartItem(userId, productId, quantity);
        return cartRepo.save(item);
    }

    public List<CartItem> getCart(String userId) {
        return cartRepo.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartRepo.deleteByUserId(userId);
    }
}
