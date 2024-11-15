package com.example.carRental.service;

import com.example.carRental.dto.CharacteristicDto;
import com.example.carRental.entity.Characteristic;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.repository.CharacteristicRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacteristicService {

    private static final Logger LOGGER = LogManager.getLogger(CharacteristicService.class);

    private static final String CREATE = "Create: ";
    private static final String NEW_CHARACTERISTIC_CREATED = "New Characteristic created.";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";
    private static final String FIND_ALL_BY_ID = "Find All by ID: ";
    private static final String CHARACTERISTIC_UPDATED_BY_ID = "Characteristic updated by id %s";
    private static final String CHARACTERISTIC_DELETED_BY_ID = "Characteristic deleted by id %s";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "Missing values: ";
    private static final String CHARACTERISTIC_DOES_NOT_EXIST_BY_ID = "Characteristic with id %s does not exist";


    @Autowired
    CharacteristicRepository characteristicRepository;

    @Transactional
    public Characteristic addCharacteristic(CharacteristicDto characteristicDto) throws MissingValuesException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        Characteristic characteristicEntity = dtoToEntity(characteristicDto);

        checkIfMissingValues(characteristicEntity, CREATE);

        LOGGER.info(NEW_CHARACTERISTIC_CREATED);
        return characteristicRepository.save(characteristicEntity);
    }
    @Transactional
    public Characteristic addCharacteristic(Characteristic characteristic) throws MissingValuesException {
        LOGGER.info(STARTING_PROCESS + CREATE);

        checkIfMissingValues(characteristic, CREATE);

        LOGGER.info(NEW_CHARACTERISTIC_CREATED);
        return characteristicRepository.save(characteristic);
    }

    public Characteristic findCharacteristicById(Long idCharacteristic) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idCharacteristic, FIND_BY_ID);
        return characteristicRepository.findById(idCharacteristic).
                orElseThrow(() -> new NotInDataBaseException(String.format(CHARACTERISTIC_DOES_NOT_EXIST_BY_ID, idCharacteristic)));
    }

    @Transactional
    public Characteristic updateCharacteristic(CharacteristicDto updatedCharacteristic, Long idCharacteristicToUpdate) throws NotInDataBaseException, MissingValuesException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idCharacteristicToUpdate, UPDATE);

        Characteristic characteristicOriginal = findCharacteristicById(idCharacteristicToUpdate);

        checkIfMissingValues(dtoToEntity(updatedCharacteristic), UPDATE);
        characteristicOriginal.setTitle(updatedCharacteristic.getTitle());
        characteristicOriginal.setIdCharacteristic(idCharacteristicToUpdate);

        LOGGER.info(String.format(CHARACTERISTIC_UPDATED_BY_ID, idCharacteristicToUpdate));
        return characteristicRepository.save(characteristicOriginal);
    }

    public void deleteCharacteristicById(Long idCharacteristic) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(idCharacteristic, DELETE);

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + DELETE);
        characteristicRepository.deleteById(idCharacteristic);
    }

    public List<Characteristic> listAllCharacteristics() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return characteristicRepository.findAll();
    }

    public List<Characteristic> findAllById(List<Long> idCharacteristicList) throws NotInDataBaseException{

        try {
            idCharacteristicList.forEach(idCharacteristic -> {
                try {
                    existsById(idCharacteristic, FIND_ALL_BY_ID);
                } catch (NotInDataBaseException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (RuntimeException notInDataBaseException) {
            LOGGER.error(FIND_ALL_BY_ID + FAILED_BECAUSE + CHARACTERISTIC_DOES_NOT_EXIST_BY_ID);
            throw new NotInDataBaseException(FIND_ALL_BY_ID + FAILED_BECAUSE + CHARACTERISTIC_DOES_NOT_EXIST_BY_ID);
        }

        return characteristicRepository.findAllById(idCharacteristicList);

    }

    public Characteristic dtoToEntity(CharacteristicDto characteristicDto) {

        Characteristic characteristicEntity = new Characteristic();

        characteristicEntity.setIdCharacteristic(characteristicDto.getIdCharacteristic());
        characteristicEntity.setTitle(characteristicDto.getTitle());

        return characteristicEntity;
    }

    public CharacteristicDto entityToDto(Characteristic characteristicEntity) {

        CharacteristicDto characteristicDto = new CharacteristicDto();

        characteristicDto.setIdCharacteristic(characteristicEntity.getIdCharacteristic());
        characteristicDto.setTitle(characteristicEntity.getTitle());

        return characteristicDto;
    }

    public void checkIfMissingValues(Characteristic characteristic, String process) throws MissingValuesException {

        if (characteristic.getTitle() == null || characteristic.getTitle().isBlank()) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES + "Characteristic title");
        }
    }

    public void existsById(Long characteristicId, String process) throws NotInDataBaseException {

        if (!characteristicRepository.existsById(characteristicId)) {
            LOGGER.error(String.format(process, FAILED_BECAUSE, CHARACTERISTIC_DOES_NOT_EXIST_BY_ID, characteristicId));
            throw new NotInDataBaseException(String.format(process + FAILED_BECAUSE + CHARACTERISTIC_DELETED_BY_ID, characteristicId));
        }
    }


}
