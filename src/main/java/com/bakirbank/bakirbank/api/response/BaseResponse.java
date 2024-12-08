package com.bakirbank.bakirbank.api.response;


import com.bakirbank.bakirbank.lib.ErrorCodeConstants;
import com.bakirbank.bakirbank.lib.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "Statu",
        "Islemkodu",
        "IslemMesaji"
})

public class BaseResponse {

    @JsonProperty("Statu")
    private String status = ResponseStatus.SUCCESS_CODE;

    @JsonProperty("IslemKodu")
    private String processCode = ErrorCodeConstants.SUCCESS;

    @JsonProperty("IslemMesaji")
    private String processMessage =  ResponseStatus.SUCCESS;

}
