package com.bank.yanki.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("yankis")
public class Yanki
{
    @Id
    private String id;
    @NotNull(message = "idDocument must not be null")
    private String idDocument;
    @NotNull(message = "phoneNumber must not be null")
    private String phoneNumber;
    @NotNull(message = "email must not be null")
    private String email;
    @NotNull(message = "mont must not be null")
    private Float mont;
}
