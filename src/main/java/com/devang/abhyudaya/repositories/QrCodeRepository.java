package com.devang.abhyudaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devang.abhyudaya.domains.entities.QrCode;
import com.devang.abhyudaya.domains.enums.QrCodeStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
    Optional<QrCode> findByTicket_IdAndTicket_Buyer_Id(UUID ticketId, UUID ticketBuyerId);

    Optional<QrCode> findByIdAndStatus(UUID id, QrCodeStatus status);
}