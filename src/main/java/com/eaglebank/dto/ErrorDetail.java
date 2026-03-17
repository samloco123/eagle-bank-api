package com.eaglebank.dto;

import lombok.Data;

@Data
public class ErrorDetail {
    private String field;
    private String message;
    private String type;
    
    public ErrorDetail(String field, String message, String type) {
        this.field = field;
        this.message = message;
        this.type = type;
    }
}