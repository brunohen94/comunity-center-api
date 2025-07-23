package com.phoebus.communitycentersapi.dtos;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AddressFormDTO {

    @NotNull(message = "Street is required")
    @NotBlank(message = "Street cannot be empty")
    private String street;

    @NotNull(message = "City is required")
    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotNull(message = "State is required")
    @NotBlank(message = "State cannot be empty")
    private String state;

    @NotNull(message = "ZipCode is required")
    @NotBlank(message = "ZipCode cannot be empty")
    private String zipCode;

}