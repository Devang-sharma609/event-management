package com.devang.abhyudaya.services;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.devang.abhyudaya.domains.dtos.request.TicketValidationRequest;
import com.devang.abhyudaya.domains.dtos.response.TicketValidationResponse;
import com.devang.abhyudaya.domains.entities.QrCode;
import com.devang.abhyudaya.domains.entities.Ticket;
import com.devang.abhyudaya.domains.entities.TicketValidation;
import com.devang.abhyudaya.domains.enums.QrCodeStatus;
import com.devang.abhyudaya.domains.enums.TicketValidationMethod;
import com.devang.abhyudaya.domains.enums.TicketValidationStatus;
import com.devang.abhyudaya.exceptions.ResourceNotFoundException;
import com.devang.abhyudaya.mappers.TicketValidationMapper;
import com.devang.abhyudaya.repositories.EventRepository;
import com.devang.abhyudaya.repositories.QrCodeRepository;
import com.devang.abhyudaya.repositories.TicketRepository;
import com.devang.abhyudaya.repositories.TicketValidationRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketValidationService {

    private final TicketValidationRepository ticketValidationRepository;
    private final QrCodeRepository qrCodeRepository;
    private final TicketRepository ticketRepository;
    private final TicketValidationMapper ticketValidationMapper;
    private final EventRepository eventRepository;

    @Transactional
    public TicketValidationResponse validateTicket(TicketValidationRequest request, UUID staffId) {
        return switch (request.getMethod()) {
            case QR_SCAN -> {
                UUID qrCodeId = request.getTargetId();

                QrCode qrCode = qrCodeRepository
                        .findByIdAndStatus(qrCodeId, QrCodeStatus.ACTIVE)
                        .orElseThrow(() -> {
                            var msg = String.format("No active QR Code found with ID: %s", qrCodeId);
                            return new ResourceNotFoundException(msg, HttpStatus.NOT_FOUND);
                        });

                Ticket ticket = qrCode.getTicket();
                yield _validate(ticket, staffId, TicketValidationMethod.QR_SCAN);
            }
            case MANUAL -> {
                UUID ticketId = request.getTargetId();

                Ticket ticket = ticketRepository
                        .findById(ticketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket",
                                "id",
                                ticketId.toString()));

                yield _validate(ticket, staffId, TicketValidationMethod.MANUAL);
            }
        };
    }

    private TicketValidationResponse _validate(Ticket ticket, UUID staffId, TicketValidationMethod method) {

         if (!isStaffAssignedToEvent(staffId, ticket.getTicketType().getEvent().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setValidationMethod(method);
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidatedAt(LocalDateTime.now());

        if (LocalDateTime.now().isAfter(ticket.getTicketType().getEvent().getEndsAt())) {
            ticketValidation.setStatus(TicketValidationStatus.EXPIRED);

            TicketValidation savedValidation = ticketValidationRepository.save(ticketValidation);

            return ticketValidationMapper.toTicketValidationResponse(savedValidation);
        } else {
            var validationStatus = ticket
                    .getValidations()
                    .stream()
                    .filter(tv -> tv.getStatus().equals(TicketValidationStatus.VALID))
                    .findFirst()
                    .map(tv -> TicketValidationStatus.VALIDATED_ALREADY)
                    .orElse(TicketValidationStatus.VALID);

            ticketValidation.setStatus(validationStatus);

            TicketValidation savedValidation = ticketValidationRepository.save(ticketValidation);

            return ticketValidationMapper.toTicketValidationResponse(savedValidation);
        }
    }

    private boolean isStaffAssignedToEvent(UUID staffId, UUID eventId) {
        return eventRepository.existsByIdAndAssignedStaffId(eventId, staffId);
    }
}
