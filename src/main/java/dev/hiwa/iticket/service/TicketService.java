package dev.hiwa.iticket.service;

import dev.hiwa.iticket.domain.dto.request.PurchaseTicketRequest;
import dev.hiwa.iticket.domain.dto.response.GetAllTickets_TicketResponse;
import dev.hiwa.iticket.domain.dto.response.GetTicketForUser_TicketResponse;
import dev.hiwa.iticket.domain.dto.response.PurchaseTicketResponse;
import dev.hiwa.iticket.domain.entities.Ticket;
import dev.hiwa.iticket.domain.entities.TicketType;
import dev.hiwa.iticket.domain.entities.User;
import dev.hiwa.iticket.domain.enums.TicketStatus;
import dev.hiwa.iticket.exceptions.ResourceNotFoundException;
import dev.hiwa.iticket.exceptions.TicketSoldOutException;
import dev.hiwa.iticket.mappers.TicketMapper;
import dev.hiwa.iticket.repository.TicketRepository;
import dev.hiwa.iticket.repository.TicketTypeRepository;
import dev.hiwa.iticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;

    private final TicketMapper ticketMapper;


    private final QrCodeService qrCodeService;

    @Transactional
    public PurchaseTicketResponse purchaseTicket(UUID userId, PurchaseTicketRequest request) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        TicketType ticketType = ticketTypeRepository
                .findByIdWithLock(request.getTicketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TicketType",
                                                                 "id",
                                                                 request.getTicketTypeId().toString()
                ));

        int purchasedTicketsCount =
                ticketRepository.countByTicketStatusAndTicketType_Id(TicketStatus.PURCHASED,
                                                                     request.getTicketTypeId()
                );
        if (ticketType.getTotalAvailable() - purchasedTicketsCount <= 0) {
            throw new TicketSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setBuyer(user);
        ticket.setTicketType(ticketType);
        ticket.setTicketStatus(TicketStatus.PURCHASED);

        Ticket savedTicket = ticketRepository.save(ticket);

        var qr = qrCodeService.generateQrCodeFor(savedTicket);
        savedTicket.getQrCodes().add(qr);

        return ticketMapper.toPurchaseTicketResponse(ticketRepository.save(savedTicket));
    }


    @Transactional(readOnly = true)
    public Page<GetAllTickets_TicketResponse> getAllUserTickets(
            UUID userId, Integer page, Integer size
    ) {
        var pageRequest = PageRequest.of(page, size);

        Page<Ticket> userTickets = ticketRepository.findAllByBuyer_Id(userId, pageRequest);

        return userTickets.map(ticketMapper::toGetAllTickets_TicketResponse);
    }

    @Transactional(readOnly = true)
    public GetTicketForUser_TicketResponse getTicketForUser(UUID userId, UUID id) {
        Ticket ticket = ticketRepository.findByIdAndBuyer_Id(id, userId).orElseThrow(() -> {
            String msg = String.format("No ticket with id %s found for user with id %s", id, userId);
            return new ResourceNotFoundException(msg);
        });

        return ticketMapper.toGetTicketForUser_TicketResponse(ticket);
    }
}
