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
public class GetTicketForUser_TicketResponse {

    private UUID id;
    private TicketStatus ticketStatus;
    private Double price;
    private String description;
    private String eventName;
    private String eventVenue;
    private LocalDateTime eventStartsAt;
    private LocalDateTime eventEndsAt;

}
