package com.phoebus.communitycentersapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class NegociationResourcesFormDTO {

    @NotNull(message = "Resource name is required")
    @NotBlank(message = "Resource name cannot be empty")
    @Pattern(regexp = "doctors|volunteers|medicalSuppliesKits|transportVehicles|basicFoodBaskets", message = "Invalid resource name")
    private String name;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

}