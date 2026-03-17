package com.eaglebank.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Embeddable
@Data
public class Address {
    @NotBlank(message = "Address line 1 is required")
    private String line1;
    
    private String line2;
    private String line3;
    
    @NotBlank(message = "Town is required")
    private String town;
    
    @NotBlank(message = "County is required")
    private String county;
    
    @NotBlank(message = "Postcode is required")
    private String postcode;
}
