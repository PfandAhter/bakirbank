package com.bakirbank.bakirbank.api.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class MoneyProcessFailed extends SendNotificationRequest{

    private String errorCode;
}