package com.example.carRental.repository;

import com.example.carRental.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value= "SELECT * FROM booking WHERE id_vehicle = ?1 AND start_date >= CURRENT_DATE() AND end_date >= CURRENT_DATE()", nativeQuery = true)
    List<Booking> findAllByProductId(@Param("id") Long idVehicle);

    @Query(value = "SELECT * FROM booking where user_id = ?1", nativeQuery = true)
    List<Booking> findAllByUserId(@Param("id") Long userId);
}
