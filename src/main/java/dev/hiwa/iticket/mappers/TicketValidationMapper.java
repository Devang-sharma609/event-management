package dev.hiwa.iticket.mappers;

import dev.hiwa.iticket.domain.dto.response.TicketValidationResponse;
import dev.hiwa.iticket.domain.entities.TicketValidation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketValidationMapper {

    @Mapping(target = "ticketId", source = "ticket.id")
    TicketValidationResponse toTicketValidationResponse(TicketValidation ticketValidation);
}
