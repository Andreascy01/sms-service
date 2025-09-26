package com.example.sms;

import com.example.sms.dto.MessageRequest;
import com.example.sms.Message;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.example.sms.service.MessageService;
import jakarta.inject.Inject;

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
    public Response sendMessage(@Valid MessageRequest request) {
        Message message = new Message();
        message.id = UUID.randomUUID();
        message.sourceNumber = request.sourceNumber;
        message.destinationNumber = request.destinationNumber;
        message.content = request.content;
        message.status = "PENDING";
        message.createdAt = LocalDateTime.now();
        message.updatedAt = LocalDateTime.now();

        message.persist(); // Panache persists it to the DB

        // Trigger async delivery simulation
        messageService.simulateDeliveryAsync(message.id);

        return Response.status(Response.Status.ACCEPTED).entity(message).build();
    }

    @GET
    @Path("/{id}")
    public Response getMessage(@PathParam("id") UUID id) {
        Message message = Message.findById(id);
        if (message == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(message).build();
    }

    @GET
    public Response getMessages(
            @QueryParam("status") String status,
            @QueryParam("sourceNumber") String sourceNumber,
            @QueryParam("destinationNumber") String destinationNumber) {

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
    public Response simulateMessage(@PathParam("id") UUID id) {
        Message message = Message.findById(id);

        if (message == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"Error\":\"Message not found\"}").build();
        }

        messageService.simulateDelivery(id);
        return Response.ok(message).build();
    }

}
