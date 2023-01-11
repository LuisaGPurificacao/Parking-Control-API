package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto dto) {
        if (service.existsByLicensePlateCar(dto.getLicensePlateCar()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        if (service.existsByParkingSpotNumber(dto.getParkingSpotNumber()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        if (service.existsByApartmentAndBlock(dto.getApartment(), dto.getBlock()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        ParkingSpotModel model = new ParkingSpotModel();
        BeanUtils.copyProperties(dto, model);
        model.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(model));
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id) {
        Optional<ParkingSpotModel> parkingSpotOptional = service.findById(id);
        return parkingSpotOptional.<ResponseEntity<Object>>map(parkingSpotModel ->
                ResponseEntity.status(HttpStatus.OK).body(parkingSpotModel)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found."));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id) {
        Optional<ParkingSpotModel> parkingSpotOptional = service.findById(id);
        if (parkingSpotOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        service.delete(parkingSpotOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted succesfully.");
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid ParkingSpotDto dto) {
        Optional<ParkingSpotModel> optional = service.findById(id);
        if (optional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        ParkingSpotModel model = new ParkingSpotModel();
        BeanUtils.copyProperties(dto, model);
        model.setId(optional.get().getId());
        model.setRegistrationDate(optional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(service.save(model));
    }

}
