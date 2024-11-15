package com.example.carRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "vehicle_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idVehicleType;
    private String title;
    private String details;
}