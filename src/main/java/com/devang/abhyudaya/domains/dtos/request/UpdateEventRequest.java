package com.devang.abhyudaya.domains.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.devang.abhyudaya.domains.enums.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @JsonProperty(value = "name")
    @NotBlank
    private String name;

    @JsonProperty(value = "venue")
    @NotBlank
    private String venue;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "starts_at")
    @NotNull
    private LocalDateTime startsAt;

    @JsonProperty(value = "ends_at")
    @NotNull
    private LocalDateTime endsAt;

    @JsonProperty(value = "sales_starts_at")
    private LocalDateTime salesStartsAt;

    @JsonProperty(value = "sales_ends_at")
    private LocalDateTime salesEndsAt;

    @JsonProperty(value = "event_status")
    @NotNull
    private EventStatus eventStatus;

    @JsonProperty(value = "ticket_types")
    @NotNull
    @Size(min = 1, message = "ticket_types must contain at least one item")
    @Valid
    private List<UpdateTicketTypeRequest> ticketTypes = new ArrayList<>();

}
