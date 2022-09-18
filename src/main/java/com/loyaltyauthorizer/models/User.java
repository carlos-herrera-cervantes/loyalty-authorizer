package com.loyaltyauthorizer.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.loyaltyauthorizer.enums.Role;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {

    @Id
    @MongoId(FieldType.OBJECT_ID)
    String id;

    @Field("email")
    String email;

    @Field("password")
    String password;

    @Field("deactivated")
    boolean deactivated;

    @Field("roles")
    String[] roles = {Role.VIEWER};

    @Field("created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    LocalDateTime updatedAt = LocalDateTime.now();

}
