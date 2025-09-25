package com.devang.abhyudaya.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.devang.abhyudaya.domains.entities.QrCode;
import com.devang.abhyudaya.domains.entities.Ticket;
import com.devang.abhyudaya.domains.enums.QrCodeStatus;
import com.devang.abhyudaya.exceptions.QrCodeGenerationException;
import com.devang.abhyudaya.exceptions.ResourceNotFoundException;
import com.devang.abhyudaya.mappers.QrCodeMapper;
import com.devang.abhyudaya.repositories.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class QrCodeService {

    private static final int QR_HEIGHT = 300;
    private static final int QR_WIDTH = 300;

    private final QrCodeRepository qrCodeRepository;
    public final QrCodeMapper qrCodeMapper;

    private final QRCodeWriter qrCodeWriter;

    public QrCode generateQrCodeFor(Ticket ticket) {
        try {
            UUID qrId = UUID.randomUUID();
            String qrCoedImage = _generateQrCodeImage(qrId);

            var qrCode = new QrCode();
            qrCode.setId(qrId);
            qrCode.setValue(qrCoedImage);
            qrCode.setStatus(QrCodeStatus.ACTIVE);
            qrCode.setTicket(ticket);

            return qrCodeRepository.saveAndFlush(qrCode);

        } catch (WriterException | IOException ex) {
            throw new QrCodeGenerationException(ex.getMessage());
        }
    }

    public byte[] getQrCodeImageForTicket(UUID userId, UUID ticketId) {
        QrCode qrCode = qrCodeRepository
                .findByTicket_IdAndTicket_Buyer_Id(ticketId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("QR Code Not found", HttpStatus.NOT_FOUND));

        try {
            return Base64.getDecoder().decode(qrCode.getValue());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid base64 QR Code for ticket ID: {}", ticketId, ex);
            throw new IllegalArgumentException("Invalid base64 QR Code for ticket ID: " + ticketId, ex);
        }

    }

    private String _generateQrCodeImage(UUID uuid) throws WriterException, IOException {
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(uuid.toString(), BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();

                return Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (WriterException | IOException ex) {
            log.error("Error generating QR Code image for UUID: {}", uuid, ex);
            throw ex;
        }
    }
}
