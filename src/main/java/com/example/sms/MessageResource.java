package com.example.sms;

import com.example.sms.dto.MessageRequest;
import com.example.sms.Message;
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

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    MessageService messageService;

    @POST
    @Transactional
    @Operation(summary = "Send a message", description = "Creates a new message with status PENDING and simulates sending it asynchronously.")
    @APIResponse(responseCode = "202", description = "Message accepted for processing", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)))
    @APIResponse(responseCode = "400", description = "Invalid request body")
    public Response sendMessage(@Valid MessageRequest request) {
        Message message = new Message();
        message.id = UUID.randomUUID();
        message.sourceNumber = request.sourceNumber;
        message.destinationNumber = request.destinationNumber;
        message.content = request.content;
        message.status = "PENDING";
        message.createdAt = LocalDateTime.now();
        message.updatedAt = LocalDateTime.now();

        message.persist();

        messageService.simulateDeliveryAsync(message.id);

        return Response.status(Response.Status.ACCEPTED).entity(message).build();
    }

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
