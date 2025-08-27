package dev.hiwa.iticket.service;

import dev.hiwa.iticket.domain.dto.request.TicketValidationRequest;
import dev.hiwa.iticket.domain.dto.response.TicketValidationResponse;
import dev.hiwa.iticket.domain.entities.QrCode;
import dev.hiwa.iticket.domain.entities.Ticket;
import dev.hiwa.iticket.domain.entities.TicketValidation;
import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import dev.hiwa.iticket.domain.enums.TicketValidationMethod;
import dev.hiwa.iticket.domain.enums.TicketValidationStatus;
import dev.hiwa.iticket.exceptions.ResourceNotFoundException;
import dev.hiwa.iticket.mappers.TicketValidationMapper;
import dev.hiwa.iticket.repository.QrCodeRepository;
import dev.hiwa.iticket.repository.TicketRepository;
import dev.hiwa.iticket.repository.TicketValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketValidationService {

    private final TicketValidationRepository ticketValidationRepository;
    private final QrCodeRepository qrCodeRepository;
    private final TicketRepository ticketRepository;

    private final TicketValidationMapper ticketValidationMapper;

    @Transactional
    public TicketValidationResponse validateTicket(TicketValidationRequest request) {
        return switch (request.getMethod()) {
            case QR_SCAN -> {
                UUID qrCodeId = request.getTargetId();

                QrCode qrCode = qrCodeRepository
                        .findByIdAndStatus(qrCodeId, QrCodeStatus.ACTIVE)
                        .orElseThrow(() -> {
                            var msg = String.format("No active QR Code found with ID: %s", qrCodeId);
                            return new ResourceNotFoundException(msg);
                        });

                Ticket ticket = qrCode.getTicket();
                yield _validate(ticket, TicketValidationMethod.QR_SCAN);
            }
            case MANUAL -> {
                UUID ticketId = request.getTargetId();

                Ticket ticket = ticketRepository
                        .findById(ticketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket",
                                                                         "id",
                                                                         ticketId.toString()
                        ));

                yield _validate(ticket, TicketValidationMethod.MANUAL);
            }
        };
    }


    private TicketValidationResponse _validate(Ticket ticket, TicketValidationMethod method) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setValidationMethod(method);
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidatedAt(LocalDateTime.now());

        // Ticket can only be validated once
        // So if the ticket has been validated before, status will be INVALID
        var validationStatus = ticket
                .getValidations()
                .stream()
                .filter(tv -> tv.getStatus().equals(TicketValidationStatus.VALID))
                .findFirst()
                .map(tv -> TicketValidationStatus.INVALID)
                .orElse(TicketValidationStatus.VALID);

        ticketValidation.setStatus(validationStatus);

        TicketValidation savedValidation = ticketValidationRepository.save(ticketValidation);

        return ticketValidationMapper.toTicketValidationResponse(savedValidation);
    }
}
