package com.phoebus.communitycentersapi.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AverageResourcesResponse {
    private double doctors;
    private double volunteers;
    private double medicalSuppliesKits;
    private double transportVehicles;
    private double basicFoodBaskets;
}