package com.example.sms;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.example.sms.data.Message;
import com.example.sms.dto.MessageRequest;
import com.example.sms.service.MessageService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST resource exposing the messaging API.
 * 
 * Responsibilities:
 * - Accept new messages (validating the request)
 * - Persist messages into the database
 * - Simulate delivery asynchronously
 * - Provide endpoints to fetch or filter messages
 */

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    MessageService messageService;

    /**
     * Send a new message.
     *
     * @param request Message payload containing source, destination and content
     * @return Response containing the persisted message with status PENDING
     */

    @POST
    @Transactional
    @Operation(summary = "Send a message", description = "Creates a new message with status PENDING and simulates sending it asynchronously.")
    @APIResponse(responseCode = "202", description = "Message accepted for processing", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)))
    @APIResponse(responseCode = "400", description = "Invalid request body")
    public Response sendMessage(
            @RequestBody(description = "Message request payload", required = true, content = @Content(schema = @Schema(implementation = MessageRequest.class))) @Valid MessageRequest request) {
        Message message = new Message();
        message.id = UUID.randomUUID();
        message.sourceNumber = request.sourceNumber;
        message.destinationNumber = request.destinationNumber;
        message.content = request.content;
        message.status = "PENDING";
        message.createdAt = LocalDateTime.now();
        message.updatedAt = LocalDateTime.now();

        // Save to database using Panache
        message.persist();

        // Trigger async delivery simulation (will later update status)
        messageService.simulateDeliveryAsync(message.id);

        return Response.status(Response.Status.ACCEPTED).entity(message).build();
    }

    /**
     * Get a single message by its ID.
     *
     * @param id UUID of the message
     * @return Response containing the message or 404 if not found
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get a message by ID", description = "Retrieve a single message by its UUID.")
    @APIResponse(responseCode = "200", description = "Message found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)))
    @APIResponse(responseCode = "404", description = "Message not found")
    public Response getMessage(
            @Parameter(description = "UUID of the message") @PathParam("id") UUID id) {
        Message message = Message.findById(id);
        if (message == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(message).build();
    }

    /**
     * List all messages, optionally filtered by status, sourceNumber, or
     * destinationNumber.
     *
     * @param status            Filter messages by status (e.g., PENDING, DELIVERED,
     *                          FAILED)
     * @param sourceNumber      Filter messages by the sender's number
     * @param destinationNumber Filter messages by the receiver's number
     * @return Response containing the list of matching messages
     */

    @GET
    @Operation(summary = "List messages", description = "Retrieve all messages, optionally filtered by status, sourceNumber, or destinationNumber.")
    public Response getMessages(
            @Parameter(description = "Filter by message status") @QueryParam("status") String status,
            @Parameter(description = "Filter by source phone number") @QueryParam("sourceNumber") String sourceNumber,
            @Parameter(description = "Filter by destination phone number") @QueryParam("destinationNumber") String destinationNumber) {

        if (status != null && !status.isBlank()) {
            return Response.ok(Message.list("status", status)).build();
        }
        if (sourceNumber != null && !sourceNumber.isBlank()) {
            return Response.ok(Message.list("sourceNumber", sourceNumber)).build();
        }
        if (destinationNumber != null && !destinationNumber.isBlank()) {
            return Response.ok(Message.list("destinationNumber", destinationNumber)).build();
        }

        return Response.ok(Message.listAll()).build();
    }

    /**
     * Manually simulate message delivery for a given message.
     *
     * @param id UUID of the message
     * @return Response with updated message or 404 if not found
     */
    @PUT
    @Path("/{id}/simulate")
    @Operation(summary = "Simulate message delivery", description = "Forces a delivery simulation for a specific message.")
    @APIResponse(responseCode = "200", description = "Simulation executed")
    @APIResponse(responseCode = "404", description = "Message not found")
    public Response simulateMessage(
            @Parameter(description = "UUID of the message to simulate") @PathParam("id") UUID id) {
        Message message = Message.findById(id);

        if (message == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"Error\":\"Message not found\"}").build();
        }

        messageService.simulateDelivery(id);
        return Response.ok(message).build();
    }
}
