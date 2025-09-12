package dev.hiwa.iticket.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPublishedEventDetailsResponse {
    private UUID id;
    private String name;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String venue;
    private List<GetPublishedEventDetails_TicketTypeResponse> ticketTypes = new ArrayList<>();
}
