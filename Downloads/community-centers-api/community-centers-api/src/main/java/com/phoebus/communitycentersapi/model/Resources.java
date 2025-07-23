package com.phoebus.communitycentersapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resources {
    private int doctors;
    private int volunteers;
    private int medicalSuppliesKits;
    private int transportVehicles;
    private int basicFoodBaskets;
}