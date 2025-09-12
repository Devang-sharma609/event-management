package dev.hiwa.iticket.repository;

import dev.hiwa.iticket.domain.entities.QrCode;
import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
    Optional<QrCode> findByTicket_IdAndTicket_Buyer_Id(UUID ticketId, UUID ticketBuyerId);

    Optional<QrCode> findByIdAndStatus(UUID id, QrCodeStatus status);
}