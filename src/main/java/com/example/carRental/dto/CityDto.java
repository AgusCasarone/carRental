package com.example.carRental.dto;

import com.example.carRental.entity.Country;
import com.example.carRental.entity.Province;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    Long idCity;
    String cityName;
    String latitud;
    String longitud;
    Province province;
    Country country;
}
