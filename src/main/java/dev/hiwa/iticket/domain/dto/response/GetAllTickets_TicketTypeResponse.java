package dev.hiwa.iticket.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTickets_TicketTypeResponse {

    private UUID id;
    private String name;
    private Double price;

}
