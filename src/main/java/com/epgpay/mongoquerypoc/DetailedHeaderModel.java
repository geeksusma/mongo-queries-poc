package com.epgpay.mongoquerypoc;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "headers")
@EqualsAndHashCode(of = "id")
class DetailedHeaderModel {

    @Id
    @Field("id")
    private String id;

    @Field("customer")
    private CustomerModel customer;

    @Field("status")
    private HeaderStatus status;

    @Field("summary")
    private String summary;

    @Field("due_date")
    private Instant dueDate;

    @CreatedDate
    @Field("create_date")
    private Instant createdDate;

    @Field("code")
    private String code;
}
