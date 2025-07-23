package com.phoebus.communitycentersapi.repository;

import com.phoebus.communitycentersapi.model.CommunityCenter;
import com.phoebus.communitycentersapi.response.AverageResourcesResponse;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityCenterRepository extends MongoRepository<CommunityCenter, String> {
    boolean existsByName(String name);
    Optional<CommunityCenter> findByName(String name);

    @Aggregation(pipeline = {
            "{ $match: { 'capacity': { $exists: true }, 'currentOccupancy': { $exists: true } } }",
            "{ $project: { id: 1, name: 1, address: 1, capacity: 1, currentOccupancy: 1, resources: 1, occupancyRate: { $divide: [ '$currentOccupancy', '$capacity' ] } } }",
            "{ $match: { occupancyRate: { $gt: 0.9 } } }"
    })
    List<CommunityCenter> findHighOccupancyCenters();

    @Aggregation(pipeline = {
            "{ $project: { doctors: '$resources.doctors', volunteers: '$resources.volunteers', medicalSuppliesKits: '$resources.medicalSuppliesKits', transportVehicles: '$resources.transportVehicles', basicFoodBaskets: '$resources.basicFoodBaskets' } }",
            "{ $group: { _id: null, doctors: { $avg: '$doctors' }, volunteers: { $avg: '$volunteers' }, medicalSuppliesKits: { $avg: '$medicalSuppliesKits' }, transportVehicles: { $avg: '$transportVehicles' }, basicFoodBaskets: { $avg: '$basicFoodBaskets' } } }"
    })
    AverageResourcesResponse getAverageResources();
}