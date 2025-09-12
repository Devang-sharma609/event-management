package dev.hiwa.iticket.exceptions;

public class ResourceConstraintViolationException extends RuntimeException {

    public ResourceConstraintViolationException(String message) {
        super(message);
    }
}
