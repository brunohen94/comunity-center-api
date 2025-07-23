package com.phoebus.communitycentersapi.service;

import com.phoebus.communitycentersapi.dtos.AddressFormDTO;
import com.phoebus.communitycentersapi.dtos.CreateCommunityCenterFormDTO;
import com.phoebus.communitycentersapi.dtos.ResourcesFormDTO;
import com.phoebus.communitycentersapi.dtos.UpdateOccupancyFormDTO;
import com.phoebus.communitycentersapi.model.Address;
import com.phoebus.communitycentersapi.model.CommunityCenter;
import com.phoebus.communitycentersapi.model.Resources;
import com.phoebus.communitycentersapi.providers.rabbitMQ.RabbitMQProducer;
import com.phoebus.communitycentersapi.repository.CommunityCenterRepository;
import com.phoebus.communitycentersapi.response.AverageResourcesResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class CommunityCenterServiceTest {

    @InjectMocks
    private CommunityCenterService communityCenterService;

    @Mock
    private CommunityCenterRepository communityCenterRepository;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @Test
    public void shouldCreateCommunityCenter() throws Exception {
        CreateCommunityCenterFormDTO formDTO = new CreateCommunityCenterFormDTO();
        formDTO.setName("Centro Comunitário Teste");

        AddressFormDTO addressDTO = new AddressFormDTO();
        addressDTO.setStreet("Rua X");
        addressDTO.setCity("Cidade Y");
        addressDTO.setState("Estado Z");
        addressDTO.setZipCode("12345-678");

        formDTO.setAddress(addressDTO);
        formDTO.setCapacity(100);
        formDTO.setCurrentOccupancy(50);

        ResourcesFormDTO resourcesDTO = new ResourcesFormDTO();
        resourcesDTO.setDoctors(5);
        resourcesDTO.setVolunteers(10);
        resourcesDTO.setMedicalSuppliesKits(20);
        resourcesDTO.setTransportVehicles(3);
        resourcesDTO.setBasicFoodBaskets(100);
        formDTO.setResources(resourcesDTO);

        CommunityCenter createdCenter = new CommunityCenter(
                "123",
                "Centro Comunitário Teste",
                new Address("Rua X", "Cidade Y", "Estado Z", "12345-678"),
                100,
                50,
                new Resources(5, 10, 20, 3, 100)
        );

        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class)))
                .thenReturn(createdCenter);

        CommunityCenter result = communityCenterService.create(formDTO);

        assertNotNull(result);
        assertEquals("Centro Comunitário Teste", result.getName());
        assertEquals("Rua X", result.getAddress().getStreet());
        assertEquals(5, result.getResources().getDoctors());
        Mockito.verify(communityCenterRepository, times(1)).save(any(CommunityCenter.class));
    }

    @Test
    public void shouldUpdateOccupancySuccessfully() {
        String centerName = "Centro Teste";
        CommunityCenter communityCenter = new CommunityCenter(
                "123",
                centerName,
                new Address("Rua X", "Cidade Y", "Estado Z", "12345-678"),
                100,
                50,
                new Resources()
        );

        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(70);

        Mockito.when(communityCenterRepository.findByName(centerName)).thenReturn(Optional.of(communityCenter));
        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class))).thenReturn(communityCenter);

        CommunityCenter result = communityCenterService.updateOccupancy(centerName, formDTO);

        assertNotNull(result);
        assertEquals(70, result.getCurrentOccupancy());
        Mockito.verify(communityCenterRepository, times(1)).findByName(centerName);
        Mockito.verify(communityCenterRepository, times(1)).save(communityCenter);
        Mockito.verify(rabbitMQProducer, never()).sendMessage(any(String.class));
    }

    @Test
    public void shouldThrowExceptionWhenOccupancyExceedsCapacity() {
        String centerName = "Centro Teste";
        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(120);

        CommunityCenter communityCenter = new CommunityCenter(
                "123",
                centerName,
                new Address("Rua X", "Cidade Y", "Estado Z", "12345-678"),
                100,
                50,
                new Resources()
        );

        Mockito.when(communityCenterRepository.findByName(centerName)).thenReturn(Optional.of(communityCenter));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            communityCenterService.updateOccupancy(centerName, formDTO);
        });

        assertEquals("New occupancy is greater than capacity", exception.getMessage());
        Mockito.verify(communityCenterRepository, times(1)).findByName(centerName);
        Mockito.verify(communityCenterRepository, never()).save(any(CommunityCenter.class));
        Mockito.verify(rabbitMQProducer, never()).sendMessage(any(String.class));
    }

    @Test
    public void shouldListHighOccupancyCenters() throws Exception {
        List<CommunityCenter> centers = Arrays.asList(
                new CommunityCenter("1", "Centro A", new Address("Rua A", "Cidade A", "Estado A", "11111-111"), 100, 95, new Resources()),
                new CommunityCenter("2", "Centro B", new Address("Rua B", "Cidade B", "Estado B", "22222-222"), 200, 190, new Resources())
        );

        Mockito.when(communityCenterRepository.findHighOccupancyCenters()).thenReturn(centers);

        List<CommunityCenter> result = communityCenterService.listHighOccupancyCenters();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Centro A", result.get(0).getName());
        assertEquals("Centro B", result.get(1).getName());

        Mockito.verify(communityCenterRepository, times(1)).findHighOccupancyCenters();
    }

    @Test
    public void shouldGetAverageResources() throws Exception {
        AverageResourcesResponse response = new AverageResourcesResponse(2.5, 5.0, 7.0, 3.5, 10.0);

        Mockito.when(communityCenterRepository.getAverageResources()).thenReturn(response);

        AverageResourcesResponse result = communityCenterService.getAverageResources();

        assertNotNull(result);
        assertEquals(2.5, result.getDoctors());
        assertEquals(5.0, result.getVolunteers());
        assertEquals(7.0, result.getMedicalSuppliesKits());
        assertEquals(3.5, result.getTransportVehicles());
        assertEquals(10.0, result.getBasicFoodBaskets());

        Mockito.verify(communityCenterRepository, times(1)).getAverageResources();
    }

    @Test
    public void shouldGenerateNotificationWhenReaching90PercentOccupancy() {
        String centerName = "Centro Notificacao";
        CommunityCenter communityCenter = new CommunityCenter(
                "456",
                centerName,
                new Address("Rua Y", "Cidade Z", "Estado W", "98765-432"),
                100,
                80,
                new Resources()
        );

        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(95);

        Mockito.when(communityCenterRepository.findByName(centerName)).thenReturn(Optional.of(communityCenter));
        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class))).thenReturn(communityCenter);

        communityCenterService.updateOccupancy(centerName, formDTO);

        String expectedMessage = "Community Center '" + centerName + "' has reached a critical capacity: 95/100.";
        Mockito.verify(rabbitMQProducer, times(1)).sendMessage(eq(expectedMessage));
        Mockito.verify(communityCenterRepository, times(1)).save(communityCenter);
        assertEquals(95, communityCenter.getCurrentOccupancy());
    }

    @Test
    public void shouldNotGenerateNotificationIfAlreadyAbove90PercentOccupancy() {
        String centerName = "Centro Ja Cheio";
        CommunityCenter communityCenter = new CommunityCenter(
                "789",
                centerName,
                new Address("Rua Z", "Cidade W", "Estado V", "11223-344"),
                100,
                91,
                new Resources()
        );

        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(95);

        Mockito.when(communityCenterRepository.findByName(centerName)).thenReturn(Optional.of(communityCenter));
        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class))).thenReturn(communityCenter);

        communityCenterService.updateOccupancy(centerName, formDTO);

        Mockito.verify(rabbitMQProducer, never()).sendMessage(any(String.class));
        Mockito.verify(communityCenterRepository, times(1)).save(communityCenter);
        assertEquals(95, communityCenter.getCurrentOccupancy());
    }

    @Test
    public void shouldGenerateNotificationWhenReachingFullCapacity() {
        String centerName = "Centro Lotado";
        CommunityCenter communityCenter = new CommunityCenter(
                "101",
                centerName,
                new Address("Rua AA", "Cidade BB", "Estado CC", "11111-000"),
                100,
                99,
                new Resources()
        );

        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(100);

        Mockito.when(communityCenterRepository.findByName(centerName)).thenReturn(Optional.of(communityCenter));
        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class))).thenReturn(communityCenter);

        communityCenterService.updateOccupancy(centerName, formDTO);

        String expectedMessage = "Community Center '" + centerName + "' has reached a critical capacity: 100/100.";
        Mockito.verify(rabbitMQProducer, times(1)).sendMessage(eq(expectedMessage));
        Mockito.verify(communityCenterRepository, times(1)).save(communityCenter);
        assertEquals(100, communityCenter.getCurrentOccupancy());
    }
}