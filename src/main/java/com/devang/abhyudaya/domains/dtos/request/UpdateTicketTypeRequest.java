package com.devang.abhyudaya.domains.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketTypeRequest {

    @JsonProperty(value = "ticket_type_id")
    @NotNull(message = "ID cannot be null")
    private UUID id;

    @JsonProperty(value = "name")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @JsonProperty(value = "price")
    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be zero or greater")
    private Double price;

    @JsonProperty(value = "total_available")
    @NotNull(message = "Total available tickets cannot be null")
    private Integer totalAvailable;
}