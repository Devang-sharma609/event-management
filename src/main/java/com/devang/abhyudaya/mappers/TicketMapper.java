package com.devang.abhyudaya.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.devang.abhyudaya.domains.dtos.response.GetAllTickets_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.GetTicketForUser_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.PurchaseTicketResponse;
import com.devang.abhyudaya.domains.dtos.response.QrCodeResponse;
import com.devang.abhyudaya.domains.entities.Ticket;
import com.devang.abhyudaya.domains.enums.QrCodeStatus;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TicketTypeMapper.class, QrCodeMapper.class}
)
public interface TicketMapper {

    @Mapping(target = "qrCode", expression = "java(mapActiveQrCode(ticket))")
    PurchaseTicketResponse toPurchaseTicketResponse(Ticket ticket);

    GetAllTickets_TicketResponse toGetAllTickets_TicketResponse(Ticket ticket);

    @Mapping(target = "price", source = "ticketType.price")
    @Mapping(target = "description", source = "ticketType.description")
    @Mapping(target = "eventName", source = "ticketType.event.name")
    @Mapping(target = "eventVenue", source = "ticketType.event.venue")
    @Mapping(target = "eventStartsAt", source = "ticketType.event.startsAt")
    @Mapping(target = "eventEndsAt", source = "ticketType.event.endsAt")
    GetTicketForUser_TicketResponse toGetTicketForUser_TicketResponse(Ticket ticket);

    default QrCodeResponse mapActiveQrCode(Ticket ticket) {
        return ticket
                .getQrCodes()
                .stream()
                .filter(qr -> qr.getStatus() == QrCodeStatus.ACTIVE)
                .findFirst()
                .map(qr -> new QrCodeResponse(qr.getId(), qr.getValue(), qr.getStatus()))
                .orElse(null);
    }
}
