package com.devang.abhyudaya.domains.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketTypeRequest {

    @JsonProperty("name")
    @NotBlank
    private String name;
    
    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    @NotNull
    @PositiveOrZero(message = "Price must be zero or greater")
    private Double price;

    @JsonProperty("total_available")
    @NotNull
    private Integer totalAvailable;

}
