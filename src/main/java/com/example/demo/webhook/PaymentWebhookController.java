package com.example.ecommerce.webhook;

import com.example.ecommerce.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/webhooks")
public class PaymentWebhookController {

    private static final Logger logger = Logger.getLogger(PaymentWebhookController.class.getName());
    private final PaymentService paymentService;

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay/payment")
    public ResponseEntity<String> razorpayWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            // Verify webhook signature
            if (!verifySignature(payload, signature)) {
                logger.warning("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");
            JSONObject paymentData = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");

            if ("payment.authorized".equals(eventType) || "payment.captured".equals(eventType)) {
                handlePaymentSuccess(paymentData);
            } else if ("payment.failed".equals(eventType)) {
                handlePaymentFailure(paymentData);
            }

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            logger.severe("Error processing webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    private void handlePaymentSuccess(JSONObject paymentData) {
        String razorpayPaymentId = paymentData.getString("id");
        String orderId = paymentData.optString("receipt", "");
        String status = paymentData.getString("status");
        double amount = paymentData.getDouble("amount") / 100.0; // Razorpay returns amount in paise

        logger.info("Processing successful payment: " + razorpayPaymentId + " for order: " + orderId);
        paymentService.handleSuccess(orderId, razorpayPaymentId, status, amount);
    }

    private void handlePaymentFailure(JSONObject paymentData) {
        String razorpayPaymentId = paymentData.getString("id");
        String orderId = paymentData.optString("receipt", "");
        String errorCode = paymentData.optString("error_code", "UNKNOWN");
        String errorDescription = paymentData.optString("error_description", "Unknown error");

        logger.warning("Payment failed: " + razorpayPaymentId + " Error: " + errorCode);
        paymentService.handleFailure(orderId, razorpayPaymentId, errorCode, errorDescription);
    }

    private boolean verifySignature(String payload, String signature) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String computedSignature = Base64.getEncoder().encodeToString(hash);
        return computedSignature.equals(signature);
    }

    @PostMapping("/payment")
    public ResponseEntity<String> paymentSuccess(@RequestParam String orderId) {
        try {
            paymentService.handleSuccess(orderId, "", "SUCCESS", 0);
            return ResponseEntity.ok("Payment processed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
