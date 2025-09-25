package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.TicketStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTickets_TicketResponse {

    private UUID id;

    @JsonProperty("ticket_status")
    private TicketStatus ticketStatus;

    @JsonProperty("ticket_type")
    private GetAllTickets_TicketTypeResponse ticketType;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
