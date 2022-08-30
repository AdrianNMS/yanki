package com.bank.yanki.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransferenceType
{
    BUY(0),
    SELL(1);
    public final int value;
}
