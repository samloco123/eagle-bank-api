package com.eaglebank.dto;

import lombok.Data;
import java.util.List;

@Data
public class BadRequestErrorResponse {
    private String message;
    private List<ErrorDetail> details;
    
    public BadRequestErrorResponse(String message, List<ErrorDetail> details) {
        this.message = message;
        this.details = details;
    }
}
