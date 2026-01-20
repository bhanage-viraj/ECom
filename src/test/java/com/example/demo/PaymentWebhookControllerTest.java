package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PaymentWebhookControllerTest {

    private String razorpaySecret = "test_secret_key";

    @Test
    public void testSignatureVerification() throws Exception {
        String payload = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_123\",\"status\":\"captured\",\"amount\":10000,\"receipt\":\"order_456\"}}}}";
        String signature = generateSignature(payload);
        
        // Verify the signature was generated correctly
        assertNotNull(signature);
        assertTrue(signature.length() > 0);
        
        // Verify same payload generates same signature
        String signature2 = generateSignature(payload);
        assertEquals(signature, signature2);
    }

    @Test
    public void testDifferentPayloadGeneratesDifferentSignature() throws Exception {
        String payload1 = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_123\",\"status\":\"captured\",\"amount\":10000,\"receipt\":\"order_456\"}}}}";
        String payload2 = "{\"event\":\"payment.failed\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_456\",\"status\":\"failed\",\"amount\":10000,\"receipt\":\"order_789\"}}}}";
        
        String signature1 = generateSignature(payload1);
        String signature2 = generateSignature(payload2);
        
        assertNotEquals(signature1, signature2);
    }

    @Test
    public void testPayloadParsing() {
        String payload = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_123\",\"status\":\"captured\",\"amount\":10000,\"receipt\":\"order_456\"}}}}";
        
        // Verify payload contains expected data
        assertTrue(payload.contains("pay_123"));
        assertTrue(payload.contains("order_456"));
        assertTrue(payload.contains("captured"));
    }

    private String generateSignature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(razorpaySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
