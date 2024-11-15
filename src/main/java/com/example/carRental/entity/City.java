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
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idCity;
    private String cityName;
    private String latitud;
    private String longitud;

    @ManyToOne(fetch = FetchType.EAGER)
    private Province province;

    @ManyToOne(fetch = FetchType.EAGER)
    private Country country;
}
