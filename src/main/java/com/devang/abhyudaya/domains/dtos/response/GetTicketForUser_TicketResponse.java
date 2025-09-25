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
public class GetTicketForUser_TicketResponse {

    @JsonProperty("ticket_id")
    private UUID id;

    @JsonProperty("ticket_status")
    private TicketStatus ticketStatus;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("name")
    private String eventName;

    @JsonProperty("venue")
    private String eventVenue;

    @JsonProperty("starts_at")
    private LocalDateTime eventStartsAt;

    @JsonProperty("ends_at")
    private LocalDateTime eventEndsAt;

}
