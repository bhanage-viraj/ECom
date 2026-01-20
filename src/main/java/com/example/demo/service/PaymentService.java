package com.example.ecommerce.service;

import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.model.Order;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment createPayment(String orderId, double amount) {
        Payment payment = new Payment(orderId, amount);
        payment.setPaymentId("pay_mock_" + UUID.randomUUID());
        return paymentRepository.save(payment);
    }

    public void handleSuccess(String orderId, String razorpayPaymentId, String status, double amount) {
        try {
            Payment payment = paymentRepository.findByOrderId(orderId);
            if (payment == null) {
                payment = new Payment(orderId, amount);
            }
            payment.setStatus("SUCCESS");
            payment.setPaymentId(razorpayPaymentId);
            paymentRepository.save(payment);

            // Mark order as paid
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setPaymentStatus("PAID");
                orderRepository.save(order);
            }

            logger.info("Payment success processed for order: " + orderId);
        } catch (Exception e) {
            logger.severe("Error handling payment success: " + e.getMessage());
        }
    }

    public void handleFailure(String orderId, String razorpayPaymentId, String errorCode, String errorDescription) {
        try {
            Payment payment = paymentRepository.findByOrderId(orderId);
            if (payment != null) {
                payment.setStatus("FAILED");
                payment.setPaymentId(razorpayPaymentId);
                paymentRepository.save(payment);
            }

            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setPaymentStatus("FAILED");
                orderRepository.save(order);
            }

            logger.warning("Payment failed for order: " + orderId + ", Error: " + errorCode);
        } catch (Exception e) {
            logger.severe("Error handling payment failure: " + e.getMessage());
        }
    }
}
