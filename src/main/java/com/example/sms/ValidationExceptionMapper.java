package com.example.sms;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception mapper for handling Bean Validation errors
 * (ConstraintViolationException).
 *
 * 
 * Whenever a request body fails validation (e.g., invalid phone number format,
 * missing required fields, content too long, etc.), this class will intercept
 * the exception and return a structured JSON response with details about the
 * errors.
 * The response will have a 400 Bad Request status code.
 *
 * Example response:
 * 
 * <pre>
 * [
 *   { "field": "sourceNumber", "message": "Invalid source phone number format" },
 *   { "field": "content", "message": "Content cannot be empty" }
 * ]
 * </pre>
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    /**
     * Converts a ConstraintViolationException into a structured JSON error
     * response.
     *
     * @param exception the validation exception containing field errors
     * @return a 400 Bad Request response with a list of field + message errors
     */

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Transform each violation into a map: { "field": ..., "message": ... }
        List<Map<String, String>> errors = exception.getConstraintViolations().stream()
                .map(violation -> {
                    Map<String, String> error = new HashMap<>();
                    // Get only the last node of the property path (field name)
                    String field = violation.getPropertyPath().toString();
                    if (field.contains(".")) {
                        field = field.substring(field.lastIndexOf(".") + 1);
                    }
                    error.put("field", field);
                    error.put("message", violation.getMessage());
                    return error;
                })
                .collect(Collectors.toList());

        // Return 400 Bad Request with the error list as JSON
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errors)
                .build();
    }
}
