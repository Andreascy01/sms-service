package com.example.sms.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.util.StringUtil;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.example.sms.data.Message;

/**
 * Service layer responsible for simulating message delivery.
 *
 * Responsibilities:
 * - Asynchronously simulate message delivery after a short delay.
 * - Update the message status (DELIVERED or FAILED) in the database.
 *
 * Simulation logic:
 * - 80% chance that a message is marked as DELIVERED.
 * - 20% chance that a message is marked as FAILED with an error code.
 */
@ApplicationScoped
public class MessageService {

    // Random generator for simulating success/failure
    private final Random random = new Random();

    // Quarkus-managed executor to run async tasks
    @Inject
    Executor executor;

    /**
     * Asynchronously simulate message delivery after a small delay.
     *
     * @param messageId the ID of the message to simulate
     */
    public void simulateDeliveryAsync(UUID messageId) {
        // Run the delivery simulation on a background thread after a short delay
        CompletableFuture.runAsync(() -> {
            try {
                // Artificial delay to simulate real-world message sending
                Thread.sleep(2000); // 2 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            simulateDelivery(messageId);
        }, executor);
    }

    /**
     * Simulates message delivery and updates the status in the database.
     *
     * Rules:
     * - 80% chance → Message is DELIVERED
     * - 20% chance → Message is FAILED with "NETWORK_ERROR"
     *
     * @param messageId the ID of the message to update
     */
    @Transactional
    public void simulateDelivery(UUID messageId) {
        Message message = Message.findById(messageId);

        if (message == null) {
            return; // No such message exists, nothing to update
        }

        // Simulate processing: 80% delivered, 20% failed
        if (random.nextDouble() < 0.8) {
            message.status = "DELIVERED";
            message.errorCode = null;
        } else {
            message.status = "FAILED";
            message.errorCode = "NETWORK_ERROR";
        }

        // Update timestamp and persist changes
        message.updatedAt = LocalDateTime.now();
        message.persist();

    }
}
