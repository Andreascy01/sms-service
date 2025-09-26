package com.example.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MessageRequest {

    @NotBlank(message = "Source number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{1,14}$", message = "Invalid source phone number format")
    public String sourceNumber;

    @NotBlank(message = "Destination number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{1,14}$", message = "Invalid destination phone number format")
    public String destinationNumber;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content cannot exceed 500 characters")
    public String content;
}
