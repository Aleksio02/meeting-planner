package ru.epta.commons.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
}
