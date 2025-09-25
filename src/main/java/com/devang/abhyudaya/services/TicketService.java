package com.devang.abhyudaya.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devang.abhyudaya.domains.dtos.request.PurchaseTicketRequest;
import com.devang.abhyudaya.domains.dtos.response.GetAllTickets_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.GetTicketForUser_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.PurchaseTicketResponse;
import com.devang.abhyudaya.domains.entities.Event;
import com.devang.abhyudaya.domains.entities.Ticket;
import com.devang.abhyudaya.domains.entities.TicketType;
import com.devang.abhyudaya.domains.entities.User;
import com.devang.abhyudaya.domains.enums.TicketStatus;
import com.devang.abhyudaya.exceptions.ResourceNotFoundException;
import com.devang.abhyudaya.exceptions.TicketSoldOutException;
import com.devang.abhyudaya.mappers.TicketMapper;
import com.devang.abhyudaya.repositories.TicketRepository;
import com.devang.abhyudaya.repositories.TicketTypeRepository;
import com.devang.abhyudaya.repositories.UserRepository;

import java.time.LocalDateTime;
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
                        request.getTicketTypeId().toString()));

        int purchasedTicketsCount = ticketRepository.countByTicketStatusAndTicketType_Id(TicketStatus.PURCHASED,
                request.getTicketTypeId());
        if (ticketType.getTotalAvailable() - purchasedTicketsCount <= 0) {
            throw new TicketSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setBuyer(user);
        ticket.setTicketType(ticketType);
        ticket.setTicketStatus(TicketStatus.PURCHASED);

        Event event = ticketType.getEvent();
        user.getAttendingEvents().size();
        if (!user.getAttendingEvents().contains(event)) {
            user.getAttendingEvents().add(event);
            userRepository.save(user);
            userRepository.flush();
        }

        userRepository.save(user);
        userRepository.flush();

        Ticket savedTicket = ticketRepository.save(ticket);

        var qr = qrCodeService.generateQrCodeFor(savedTicket);
        savedTicket.getQrCodes().add(qr);

        return ticketMapper.toPurchaseTicketResponse(ticketRepository.save(savedTicket));
    }

    @Transactional(readOnly = true)
    public Page<GetAllTickets_TicketResponse> getAllUserTickets(
            UUID userId, Integer page, Integer size) {
        var pageRequest = PageRequest.of(page, size);

        Page<Ticket> userTickets = ticketRepository.findAllByBuyer_Id(userId, pageRequest);

        return userTickets.map(ticketMapper::toGetAllTickets_TicketResponse);
    }

    @Transactional(readOnly = true)
    public GetTicketForUser_TicketResponse getTicketForUser(UUID userId, UUID id) {
        Ticket ticket = ticketRepository.findByIdAndBuyer_Id(id, userId).orElseThrow(() -> {
            String msg = String.format("No ticket with id %s found for user with id %s", id, userId);
            return new ResourceNotFoundException(msg, HttpStatus.NOT_FOUND);
        });

        return ticketMapper.toGetTicketForUser_TicketResponse(ticket);
    }

    public void cancelTicket(UUID userId, UUID id) {
        Ticket ticket = ticketRepository.findByIdAndBuyer_Id(id, userId).orElseThrow(() -> {
            String msg = String.format("No ticket with id %s found for user with id %s", id, userId);
            return new ResourceNotFoundException(msg, HttpStatus.NOT_FOUND);
        });
        

        Event event = ticket.getTicketType().getEvent();
        userRepository.findById(userId).ifPresent(user -> {
            user.getAttendingEvents().remove(event);
            userRepository.save(user);
            userRepository.flush();
        });

        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }
}