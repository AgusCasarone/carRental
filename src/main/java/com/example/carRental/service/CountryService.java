package com.example.carRental.service;

import com.example.carRental.dto.CountryDto;
import com.example.carRental.entity.Country;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.repository.CountryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private static final Logger LOGGER = LogManager.getLogger(CountryService.class);

    private static final String CREATE = "Create: ";
    private static final String NEW_COUNTRY_CREATED = "New Country created.";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";
    private static final String COUNTRY_UPDATED_BY_ID = "Country updated by id %s";
    private static final String COUNTRY_DELETED_BY_ID = "Country deleted by id %s";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "Missing values: ";
    private static final String COUNTRY_DOES_NOT_EXIST_BY_ID = "Country with id %s does not exist";

    @Autowired
    CountryRepository countryRepository;

    public Country addCountry(CountryDto countryDto) throws MissingValuesException, NotInDataBaseException, WronglyPopulatedListsException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        Country countryEntity = dtoToEntity(countryDto);

        checkIfMissingValues(countryEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        LOGGER.info(NEW_COUNTRY_CREATED);
        return countryRepository.save(countryEntity);
    }

    public Country findCountryById(Long idCountry) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idCountry, FIND_BY_ID);
        return countryRepository.findById(idCountry).
                orElseThrow(() -> new NotInDataBaseException(String.format(COUNTRY_DOES_NOT_EXIST_BY_ID, idCountry)));
    }

    public Country updateCountry(CountryDto updatedCountry, Long idCountryToUpdate) throws NotInDataBaseException, MissingValuesException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idCountryToUpdate, UPDATE);

        Country countryOriginal = findCountryById(idCountryToUpdate);

        checkIfMissingValues(dtoToEntity(updatedCountry), UPDATE);
        countryOriginal.setCountryName(updatedCountry.getCountryName());
        countryOriginal.setIdCountry(idCountryToUpdate);

        LOGGER.info(String.format(COUNTRY_UPDATED_BY_ID, idCountryToUpdate));
        return countryRepository.save(countryOriginal);
    }

    public void deleteCountryById(Long idCountry) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(idCountry, DELETE);

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + DELETE);
        countryRepository.deleteById(idCountry);
    }

    public List<Country> listAllCountries() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return countryRepository.findAll();
    }

    public Country dtoToEntity(CountryDto countryDto) {

        Country countryEntity = new Country();

        countryEntity.setIdCountry(countryDto.getIdCountry());
        countryEntity.setCountryName(countryDto.getCountryName());

        return countryEntity;
    }

    public CountryDto entityToDto(Country countryEntity) {

        CountryDto countryDto = new CountryDto();

        countryDto.setIdCountry(countryEntity.getIdCountry());
        countryDto.setCountryName(countryEntity.getCountryName());

        return countryDto;
    }

    public void checkIfMissingValues(Country country, String process) throws MissingValuesException {

        if (country.getCountryName() == null || country.getCountryName().isBlank()) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES);
        }
    }

    public void existsById(Long countryId, String process) throws NotInDataBaseException {

        if (!countryRepository.existsById(countryId)) {
            LOGGER.error(String.format(process, FAILED_BECAUSE, COUNTRY_DOES_NOT_EXIST_BY_ID, countryId));
            throw new NotInDataBaseException(String.format(process + FAILED_BECAUSE + COUNTRY_DOES_NOT_EXIST_BY_ID, countryId));
        }
    }
}
