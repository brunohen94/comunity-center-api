package com.phoebus.communitycentersapi.dtos;

import com.phoebus.communitycentersapi.model.Address;
import com.phoebus.communitycentersapi.model.CommunityCenter;
import com.phoebus.communitycentersapi.model.Resources;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCommunityCenterFormDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Address is required")
    @Valid
    private AddressFormDTO address;

    @Min(value = 0, message = "Capacity must be at least 0")
    private Integer capacity;

    @Min(value = 0, message = "Current occupancy must be at least 0")
    private Integer currentOccupancy;

    @NotNull(message = "Resources are required")
    @Valid
    private ResourcesFormDTO resources;

    public CommunityCenter transformToObject(){
        return new CommunityCenter(
                null,
                getName(),
                new Address(getAddress().getStreet(),
                        getAddress().getCity(),
                        getAddress().getState(),
                        getAddress().getZipCode()
                ),
                getCapacity(),
                getCurrentOccupancy(),
                new Resources(
                        getResources().getDoctors(),
                        getResources().getVolunteers(),
                        getResources().getMedicalSuppliesKits(),
                        getResources().getTransportVehicles(),
                        getResources().getBasicFoodBaskets()
                )
        );
    }
}