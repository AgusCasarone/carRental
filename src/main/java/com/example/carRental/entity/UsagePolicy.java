package com.example.carRental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usage_policy")
public class UsagePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUsagePolicy;
    private String description;
    @Enumerated(EnumType.STRING)
    private UsagePolicyType usagePolicyType;

}
