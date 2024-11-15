package com.example.carRental.dto;

import com.example.carRental.entity.Characteristic;
import com.example.carRental.entity.City;
import com.example.carRental.entity.UsagePolicy;
import com.example.carRental.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private Long idVehicle;
    private List<Characteristic> characteristicsList;
    private VehicleType vehicleType;
    private Double pricePerDay;
    private String details;
    private String model;
    private City city;
    private List<UsagePolicy> usagePoliciesList;
    private String vehiclePlate;
}