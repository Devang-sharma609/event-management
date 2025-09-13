package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.TicketValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponse {

    @JsonProperty("ticket_id")
    private UUID ticketId;

    @JsonProperty("validation_status")
    private TicketValidationStatus status;
}
