package com.example.carRental.repository;

import com.example.carRental.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query(value= "select * from users u where email=?1", nativeQuery = true)
    Users findByEmailUser (@Param("email") String email);

}
