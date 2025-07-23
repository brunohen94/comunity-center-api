package com.phoebus.communitycentersapi.service;

import com.phoebus.communitycentersapi.dtos.CreateCommunityCenterFormDTO;
import com.phoebus.communitycentersapi.dtos.UpdateOccupancyFormDTO;
import com.phoebus.communitycentersapi.exception.utils.httpException.ConflictException;
import com.phoebus.communitycentersapi.exception.utils.httpException.NotFoundException;
import com.phoebus.communitycentersapi.model.CommunityCenter;
import com.phoebus.communitycentersapi.providers.rabbitMQ.RabbitMQProducer;
import com.phoebus.communitycentersapi.repository.CommunityCenterRepository;
import com.phoebus.communitycentersapi.response.AverageResourcesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCenterService {

    private final CommunityCenterRepository communityCenterRepository;
    private final RabbitMQProducer rabbitMQProducer; // DECLARAÇÃO CORRETA

    public CommunityCenter validateCenterExists(String centerName) {
        return communityCenterRepository.findByName(centerName)
                .orElseThrow(() -> new NotFoundException("Not Found Community Center with name '" + centerName + "'"));
    }

    @Transactional
    public CommunityCenter create(CreateCommunityCenterFormDTO form) {
        if (communityCenterRepository.existsByName(form.getName())) {
            throw new ConflictException("Community center with name '" + form.getName() + "' already exists.");
        }
        return communityCenterRepository.save(form.transformToObject());
    }

    @Transactional
    public CommunityCenter updateOccupancy(String name, UpdateOccupancyFormDTO form) {
        CommunityCenter communityCenter = validateCenterExists(name);

        if(form.getOccupancy() < 0) {
            throw new RuntimeException("Occupancy cannot be negative");
        }
        if(form.getOccupancy() > communityCenter.getCapacity()) {
            throw new RuntimeException("New occupancy is greater than capacity");
        }

        int oldOccupancy = communityCenter.getCurrentOccupancy();
        communityCenter.setCurrentOccupancy(form.getOccupancy());
        CommunityCenter updatedCenter = communityCenterRepository.save(communityCenter);

        boolean wasBelow90 = (double)oldOccupancy / communityCenter.getCapacity() <= 0.9;
        boolean nowAbove90 = (double)form.getOccupancy() / communityCenter.getCapacity() > 0.9;
        boolean isExactlyFull = form.getOccupancy().equals(communityCenter.getCapacity());

        if (isExactlyFull || (wasBelow90 && nowAbove90)) {
            String message = "Community Center '" + name + "' has reached a critical capacity: " + form.getOccupancy() + "/" + communityCenter.getCapacity() + ".";
            rabbitMQProducer.sendMessage(message);
        }
        return updatedCenter;
    }

    public List<CommunityCenter> listHighOccupancyCenters() {
        return communityCenterRepository.findHighOccupancyCenters();
    }

    public AverageResourcesResponse getAverageResources() {
        return communityCenterRepository.getAverageResources();
    }
}