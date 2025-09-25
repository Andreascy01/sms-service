package com.example.sms;

import com.example.sms.dto.MessageRequest;
import com.example.sms.Message;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

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

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(message)
                .build();
    }
}
