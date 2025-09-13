package dev.hiwa.iticket.domain.dto.request;

import dev.hiwa.iticket.domain.enums.TicketValidationMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequest {

    @JsonProperty(value = "targetId")
    @NotNull
    private UUID targetId;

    @JsonProperty(value = "method")
    @NotNull
    private TicketValidationMethod method;
}
