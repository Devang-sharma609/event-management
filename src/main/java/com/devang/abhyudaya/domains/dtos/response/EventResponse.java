package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.EventStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

    @JsonProperty("event_id")
    private UUID id;
    
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("starts_at")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("venue")
    private String venue;

    @JsonProperty("sales_starts_at")
    private LocalDateTime salesStartsAt;

    @JsonProperty("sales_ends_at")
    private LocalDateTime salesEndsAt;

    @JsonProperty("event_status")
    private EventStatus eventStatus;

    @JsonProperty("ticket_types")
    private List<TicketTypeResponse> ticketTypes = new ArrayList<>();
}
