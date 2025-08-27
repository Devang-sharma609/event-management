package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.TicketValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponse {

    private UUID ticketId;
    private TicketValidationStatus status;
}
