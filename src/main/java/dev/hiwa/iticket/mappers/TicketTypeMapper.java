package dev.hiwa.iticket.mappers;

import dev.hiwa.iticket.domain.dto.request.CreateTicketTypeRequest;
import dev.hiwa.iticket.domain.dto.request.UpdateTicketTypeRequest;
import dev.hiwa.iticket.domain.dto.response.GetAllTickets_TicketTypeResponse;
import dev.hiwa.iticket.domain.dto.response.GetPublishedEventDetails_TicketTypeResponse;
import dev.hiwa.iticket.domain.dto.response.PurchaseTicket_TicketTypeResponse;
import dev.hiwa.iticket.domain.dto.response.TicketTypeResponse;
import dev.hiwa.iticket.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketTypeMapper {

    TicketType toEntity(CreateTicketTypeRequest request);
    TicketType toEntity(UpdateTicketTypeRequest request);

    TicketTypeResponse toTicketTypeResponse(TicketType ticketType);

    GetPublishedEventDetails_TicketTypeResponse toGetPublishedEventDetailsTicketTypeResponse(TicketType ticketType);

    PurchaseTicket_TicketTypeResponse toPurchaseTicketTypeResponse(TicketType ticketType);

    GetAllTickets_TicketTypeResponse toGetAllTickets_TicketTypeResponse(TicketType ticketType);

    void update(@MappingTarget TicketType ticketType, UpdateTicketTypeRequest request);

}
