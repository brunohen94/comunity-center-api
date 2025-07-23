package com.phoebus.communitycentersapi.dtos;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;

@Getter
@Setter
public class ResourcesFormDTO {

    @Min(value = 0, message = "Doctors must be at least 0")
    private Integer doctors;

    @Min(value = 0, message = "Volunteers must be at least 0")
    private Integer volunteers;

    @Min(value = 0, message = "Medical Supplies Kits must be at least 0")
    private Integer medicalSuppliesKits;

    @Min(value = 0, message = "Transport Vehicles must be at least 0")
    private Integer transportVehicles;

    @Min(value = 0, message = "Basic Food Baskets must be at least 0")
    private Integer basicFoodBaskets;

}