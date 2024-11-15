package com.example.carRental.repository;

import com.example.carRental.entity.City;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface CityRepository extends JpaRepository<City, Long> {

    @Query(value = "select c.* "
            +"from city c "
            + "where c.city_name = ?1", nativeQuery = true)
    List<City> findCityByName(@Param("name") String name);

}
