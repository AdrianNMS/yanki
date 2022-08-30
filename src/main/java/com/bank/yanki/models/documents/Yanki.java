package com.bank.yanki.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("yankis")
public class Yanki
{
    @Id
    private String id;
    private String idDocument;
    private String phoneNumber;
    private String email;
    private Float mont;
}
