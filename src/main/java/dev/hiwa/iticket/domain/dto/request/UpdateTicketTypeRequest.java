package dev.hiwa.iticket.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketTypeRequest {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be zero or greater")
    private Double price;

    @NotNull(message = "Total available tickets cannot be null")
    private Integer totalAvailable;
}