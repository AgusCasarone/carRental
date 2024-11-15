package com.example.carRental.service;

import com.example.carRental.dto.CityDto;
import com.example.carRental.entity.City;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.repository.CityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private static final Logger LOGGER = LogManager.getLogger(CityService.class);

    private static final String CREATE = "Create: ";
    private static final String NEW_CITY_CREATED = "New City created.";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String FIND_BY_NAME = "Find by name: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";
    private static final String CITY_UPDATED_BY_ID = "City updated by id %s";
    private static final String CITY_DELETED_BY_ID = "City deleted by id %s";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "Missing values: ";
    private static final String CITY_DOES_NOT_EXIST_BY_ID = "City with id %s does not exist";
    private static final String CITY_DOES_NOT_EXIST_BY_NAME = "City with name %s does not exist";

    @Autowired
    CityRepository cityRepository;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    CountryService countryService;

    public City addCity(CityDto cityDto) throws MissingValuesException, NotInDataBaseException, WronglyPopulatedListsException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        City cityEntity = dtoToEntity(cityDto);

        checkIfMissingValues(cityEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        LOGGER.info(NEW_CITY_CREATED);
        return cityRepository.save(cityEntity);
    }

    public City findCityById(Long idCity) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idCity, FIND_BY_ID);
        return cityRepository.findById(idCity).
                orElseThrow(() -> new NotInDataBaseException(String.format(CITY_DOES_NOT_EXIST_BY_ID, idCity)));
    }

    public City updateCity(CityDto updatedCity, Long idCityToUpdate) throws NotInDataBaseException, MissingValuesException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idCityToUpdate, UPDATE);

        City cityOriginal = findCityById(idCityToUpdate);

        if (!updatedCity.getCityName().isBlank()) {
            cityOriginal.setCityName(updatedCity.getCityName());
        }
        if(!updatedCity.getLatitud().isBlank()){
            cityOriginal.setLatitud(updatedCity.getLatitud());
        }
        if(!updatedCity.getLongitud().isBlank()){
            cityOriginal.setLongitud(updatedCity.getLongitud());
        }
        if (updatedCity.getProvince() != null) {
            provinceService.existsById(updatedCity.getProvince().getIdProvince(), UPDATE);
            cityOriginal.setProvince(updatedCity.getProvince());
        }
        if (updatedCity.getCountry() != null) {
            countryService.existsById(updatedCity.getCountry().getIdCountry(), UPDATE);
            cityOriginal.setCountry(updatedCity.getCountry());
        }

        cityOriginal.setIdCity(idCityToUpdate);

        LOGGER.info(String.format(CITY_UPDATED_BY_ID, idCityToUpdate));
        return cityRepository.save(cityOriginal);
    }

    public void deleteCityById(Long idCity) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(idCity, DELETE);

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + DELETE);
        cityRepository.deleteById(idCity);
    }

    public List<City> listAllCities() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return cityRepository.findAll();
    }

    public List<City> findCityByName(String cityName) throws NotInDataBaseException {

        LOGGER.info((STARTING_PROCESS + FIND_BY_NAME));

        List<City> cities = cityRepository.findCityByName(cityName);

        if (cities.isEmpty()) {
            throw new NotInDataBaseException(
                    String.format(FAILED_BECAUSE + CITY_DOES_NOT_EXIST_BY_NAME, cityName));
        } else {
            return cities;
        }
    }

    public City dtoToEntity(CityDto cityDto) throws NotInDataBaseException {

        City cityEntity = new City();

        cityEntity.setIdCity(cityDto.getIdCity());
        cityEntity.setCityName(cityDto.getCityName());
        cityEntity.setLatitud(cityDto.getLatitud());
        cityEntity.setLongitud(cityDto.getLongitud());
        cityEntity.setProvince(provinceService.findProvinceById(cityDto.getProvince().getIdProvince()));
        cityEntity.setCountry(countryService.findCountryById(cityDto.getCountry().getIdCountry()));

        return cityEntity;
    }

    public CityDto entityToDto(City cityEntity) throws NotInDataBaseException {

        CityDto cityDto = new CityDto();

        cityDto.setIdCity(cityEntity.getIdCity());
        cityDto.setCityName(cityEntity.getCityName());
        cityDto.setLatitud(cityEntity.getLatitud());
        cityDto.setLongitud(cityEntity.getLongitud());
        cityDto.setProvince(provinceService.findProvinceById(cityEntity.getProvince().getIdProvince()));
        cityDto.setCountry(countryService.findCountryById(cityEntity.getCountry().getIdCountry()));

        return cityDto;
    }

    public void checkIfMissingValues(City city, String process) throws MissingValuesException {

        if ((city.getCityName() == null || city.getCityName().isBlank())
                || (city.getLatitud() == null || city.getLatitud().isBlank())
                || (city.getLongitud() == null || city.getLongitud().isBlank())
                || city.getProvince() == null
                || city.getCountry() == null
        ) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES);
        }
    }

    public void existsById(Long cityId, String process) throws NotInDataBaseException {

        if (!cityRepository.existsById(cityId)) {
            LOGGER.error(String.format(process, FAILED_BECAUSE, CITY_DOES_NOT_EXIST_BY_ID, cityId));
            throw new NotInDataBaseException(String.format(process + FAILED_BECAUSE + CITY_DOES_NOT_EXIST_BY_ID, cityId));
        }
    }
}
