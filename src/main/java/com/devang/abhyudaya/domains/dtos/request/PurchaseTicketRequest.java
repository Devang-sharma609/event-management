package com.devang.abhyudaya.domains.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTicketRequest {

    @JsonProperty(value = "ticketTypeId")
    @NotNull
    private UUID ticketTypeId;
}
