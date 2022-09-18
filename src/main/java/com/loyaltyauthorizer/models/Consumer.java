package com.loyaltyauthorizer.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;

@Data
@Document(collection = "consumers")
public class Consumer {

    @Id
    @MongoId(FieldType.OBJECT_ID)
    String id;

    @Field("name")
    String name;

    @Field("api_key")
    String apiKey;

    @Field("active")
    boolean active = true;

    @Field("created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    LocalDateTime updatedAt = LocalDateTime.now();

}
