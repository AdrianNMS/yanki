package com.bank.yanki.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Audit {
    @CreatedDate
    @JsonIgnore
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime updateDate;
}
