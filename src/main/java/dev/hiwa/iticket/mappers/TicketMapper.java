package dev.hiwa.iticket.mappers;

import dev.hiwa.iticket.domain.dto.response.GetAllTickets_TicketResponse;
import dev.hiwa.iticket.domain.dto.response.GetTicketForUser_TicketResponse;
import dev.hiwa.iticket.domain.dto.response.PurchaseTicketResponse;
import dev.hiwa.iticket.domain.dto.response.QrCodeResponse;
import dev.hiwa.iticket.domain.entities.Ticket;
import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

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
