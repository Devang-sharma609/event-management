package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiValidationErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;
}
