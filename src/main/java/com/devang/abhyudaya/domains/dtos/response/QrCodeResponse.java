package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.QrCodeStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    @JsonProperty("qr_code_id")
    private UUID id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("status")
    private QrCodeStatus status;

}
