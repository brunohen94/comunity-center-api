package com.phoebus.communitycentersapi.repository;

import com.phoebus.communitycentersapi.model.ResourcesPoints;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResourcePointsRepository extends MongoRepository<ResourcesPoints, String> {
    Optional<ResourcesPoints> findByName(String name);
}