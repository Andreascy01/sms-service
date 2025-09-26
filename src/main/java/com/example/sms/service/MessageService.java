package com.example.sms.service;

import com.example.sms.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.util.StringUtil;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApplicationScoped
public class MessageService {

    private final Random random = new Random();

    @Inject
    Executor executor; // Quarkus-managed executor

    public void simulateDeliveryAsync(UUID messageId) {
        // Run simulation after a short delay
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000); // 2 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            simulateDelivery(messageId);
        }, executor);
    }

    @Transactional
    public void simulateDelivery(UUID messageId) {
        Message message = Message.findById(messageId);

        if (message == null) {
            return; // nothing to update
        }

        // Simulate processing: 80% delivered, 20% failed
        if (random.nextDouble() < 0.8) {
            message.status = "DELIVERED";
            message.errorCode = null;
        } else {
            message.status = "FAILED";
            message.errorCode = "NETWORK_ERROR";
        }

        message.updatedAt = LocalDateTime.now();
        message.persist();

    }
}
