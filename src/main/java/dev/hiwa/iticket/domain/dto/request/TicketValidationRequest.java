package dev.hiwa.iticket.domain.dto.request;

import dev.hiwa.iticket.domain.enums.TicketValidationMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequest {
    @NotNull
    private UUID targetId;

    @NotNull
    private TicketValidationMethod method;
}
