package com.example.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for incoming message requests.
 *
 * This class is responsible for validating the input payload when
 * a client attempts to send a new message through the API.
 *
 * Validation rules:
 * - sourceNumber: must be a valid phone number format (E.164 style).
 * - destinationNumber: must be a valid phone number format (E.164 style).
 * - content: must not be blank and can contain up to 500 characters.
 */
public class MessageRequest {
    /**
     * The phone number of the sender.
     * 
     * Must be a valid E.164 number (e.g., +1234567890).
     * Validation:
     * - Required (cannot be null or blank).
     * - Must match the regex for international phone numbers.
     */
    @NotBlank(message = "Source number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{1,14}$", message = "Invalid source phone number format")
    public String sourceNumber;

    /**
     * The phone number of the recipient.
     * 
     * Must be a valid E.164 number (e.g., +1234567890).
     * Validation:
     * - Required (cannot be null or blank).
     * - Must match the regex for international phone numbers.
     */

    @NotBlank(message = "Destination number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{1,14}$", message = "Invalid destination phone number format")
    public String destinationNumber;

    /**
     * The content of the message to be sent.
     * 
     * Validation:
     * - Required (cannot be empty).
     * - Maximum length of 500 characters.
     */
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content cannot exceed 500 characters")
    public String content;
}
