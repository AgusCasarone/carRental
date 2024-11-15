package com.example.carRental.repository;

import com.example.carRental.entity.VehicleType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
}
