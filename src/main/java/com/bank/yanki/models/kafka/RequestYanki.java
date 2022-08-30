package com.bank.yanki.models.kafka;

import com.bank.yanki.models.enums.TransferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestYanki
{
    private String phoneNumberSender;
    private String phoneNumberReceiver;
    private Float mont;
    private String idTransference;
    private TransferenceType transferenceType;
}
