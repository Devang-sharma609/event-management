package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPublishedEventDetailsResponse {

    @JsonProperty("event_id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("starts_at")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("venue")
    private String venue;

    @JsonProperty("ticket_types")
    private List<GetPublishedEventDetails_TicketTypeResponse> ticketTypes = new ArrayList<>();
}
