package com.example.carRental.repository;

import com.example.carRental.entity.UsagePolicy;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UsagePolicyRepository extends JpaRepository<UsagePolicy, Long> {
}
