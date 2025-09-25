package com.devang.abhyudaya.domains.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.TicketValidationMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequest {

    @JsonProperty(value = "targetId")
    @NotNull
    private UUID targetId;

    @JsonProperty(value = "method")
    @NotNull
    private TicketValidationMethod method;
}
