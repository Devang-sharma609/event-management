package dev.hiwa.iticket.repository;

import dev.hiwa.iticket.domain.entities.Ticket;
import dev.hiwa.iticket.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    int countByTicketStatusAndTicketType_Id(TicketStatus ticketStatus, UUID ticketTypeId);

    Page<Ticket> findAllByBuyer_Id(UUID userId, Pageable pageable);

    Optional<Ticket> findByIdAndBuyer_Id(UUID ticketId, UUID userId);
}