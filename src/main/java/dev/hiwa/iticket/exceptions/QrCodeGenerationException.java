package dev.hiwa.iticket.exceptions;

public class QrCodeGenerationException extends RuntimeException {

    public QrCodeGenerationException(String message) {
        super(message);
    }
}
