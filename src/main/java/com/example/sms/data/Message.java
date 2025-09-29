package com.example.sms.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "source_number", nullable = false)
    public String sourceNumber;

    @Column(name = "destination_number", nullable = false)
    public String destinationNumber;

    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    public String status;

    @Column(name = "error_code")
    public String errorCode;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
