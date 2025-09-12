package dev.hiwa.iticket.exceptions;

import java.util.UUID;

public class ResourceAlreadyExistsException extends RuntimeException {

    String resourceName;
    String field;
    String fieldName;
    UUID fieldId;

    public ResourceAlreadyExistsException(
            String resourceName, String field, String fieldName
    ) {
        super(String.format("%s already exists with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceAlreadyExistsException(
            String resourceName, String field, UUID fieldId
    ) {
        super(String.format("%s already exists with %s: %s", resourceName, field, fieldId.toString()));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
