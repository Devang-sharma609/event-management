package com.devang.abhyudaya.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.devang.abhyudaya.domains.dtos.request.CreateTicketTypeRequest;
import com.devang.abhyudaya.domains.dtos.request.UpdateTicketTypeRequest;
import com.devang.abhyudaya.domains.dtos.response.GetAllTickets_TicketTypeResponse;
import com.devang.abhyudaya.domains.dtos.response.GetPublishedEventDetails_TicketTypeResponse;
import com.devang.abhyudaya.domains.dtos.response.PurchaseTicket_TicketTypeResponse;
import com.devang.abhyudaya.domains.dtos.response.TicketTypeResponse;
import com.devang.abhyudaya.domains.entities.TicketType;

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
