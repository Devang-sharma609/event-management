package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventResponse {

    private UUID id;
    private String name;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String venue;
    private LocalDateTime salesStartsAt;
    private LocalDateTime salesEndsAt;
    private EventStatus eventStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
