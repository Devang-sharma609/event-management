package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.TicketValidationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponse {

    @JsonProperty("ticket_id")
    private UUID ticketId;

    @JsonProperty("validation_status")
    private TicketValidationStatus status;
}
