package com.management.houserent.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SignatureGenerator {
    public static String generateSignature(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
        String orderId = "order_RGi0KBqQOLeqYS";
        String paymentId = "pay_TEST_123456";
        String secret = "ML98NxgUI1Ooa3Vox0y7Lfyp";

        String payload = orderId + "|" + paymentId;
        String signature = generateSignature(payload, secret);

        System.out.println("Generated Signature: " + signature);
    }
}
