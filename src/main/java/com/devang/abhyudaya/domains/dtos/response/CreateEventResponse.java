package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.EventStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventResponse {

    @JsonProperty("event_id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("venue")
    private String venue;

    @JsonProperty("event_status")
    private EventStatus eventStatus;

    @JsonProperty("starts_at")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("sales_starts_at")
    private LocalDateTime salesStartsAt;

    @JsonProperty("sales_ends_at")
    private LocalDateTime salesEndsAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
