package com.example.carRental.repository;


import com.example.carRental.entity.Vehicle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query(value = "select v.*, vt.title, vt.id_vehicle_type, vc.*, il.* from vehicle AS v\n" +
            "INNER JOIN vehicle_type AS vt on v.vehicle_type_id_vehicle_type = id_vehicle_type\n" +
            "INNER JOIN vehicle_characteristics_list vc ON v.id_vehicle = vc.vehicle_id_vehicle\n" +
            "INNER JOIN characteristic ON id_characteristic = vc.characteristics_list_id_characteristic\n" +
            "INNER JOIN vehicle_images_list il ON v.id_vehicle = il.vehicle_id_vehicle\n" +
            "INNER JOIN image ON id_image = il.images_list_id_image\n" +
            "WHERE v.vehicle_type_id_vehicle_type = :vehicleType limit 10 offset :pageOffset", nativeQuery = true)
    List<Vehicle> listPaginatedVehicles(@Param("pageOffset") int start, @Param("vehicleType") int vehicleType);
    @Query(value = "select * from vehicle order by rand()", nativeQuery = true)
    List<Vehicle> getRandomList();

    @Query(value = "select v.* "
            +"from vehicle v "
            + "inner join city c "
            + "on v.id_city = c.id_city "
            + "where c.city_name = ?1", nativeQuery = true)
    List<Vehicle> findbycity(@Param("name") String name);

    @Query(value = "select v.* "
            + "from vehicle v "
            + "where vehicle_plate=?1", nativeQuery = true)
    Vehicle findByPlate(@Param("plate") String plate);

    @Query(value="select v.* "
            + "FROM vehicle v "
            + "LEFT JOIN booking b ON v.id_vehicle = b.id_vehicle "
            + "  AND b.end_date BETWEEN ?1 AND ?2 "
            + "  AND b.start_date BETWEEN ?1 AND ?2 "
            + "WHERE b.id_vehicle IS NULL", nativeQuery = true)
    List<Vehicle> findByDates(@Param("start") String start, @Param("end") String end);

    @Query(value = "SELECT v.* " +
            "FROM vehicle v " +
            "INNER JOIN city c ON v.id_city = c.id_city " +
            "LEFT JOIN booking b ON v.id_vehicle = b.id_vehicle " +
            "AND b.end_date BETWEEN ?2 AND ?3 " +
            "AND b.start_date BETWEEN ?2 AND ?3 " +
            "WHERE c.city_name = ?1 AND b.id_vehicle IS NULL",
            nativeQuery = true)
    List<Vehicle> findByCityDate(@Param("city") String city, @Param("start") String start, @Param("end") String end);
}
