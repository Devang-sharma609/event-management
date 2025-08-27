package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTickets_TicketResponse {

    private UUID id;
    private TicketStatus ticketStatus;
    private GetAllTickets_TicketTypeResponse ticketType;
    private LocalDateTime createdAt;
}
