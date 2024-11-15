package com.example.carRental.controller;

import com.example.carRental.dto.CharacteristicDto;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.service.CharacteristicService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/characteristic")
@CrossOrigin("*")
public class CharacteristicController {

    private static final Logger LOGGER = LogManager.getLogger(CharacteristicController.class);


    private static final String CREATE = "Create: ";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List All: ";
    private static final String CHARACTERISTIC_DELETED_BY_ID = "Characteristic with id %s deleted";
    private static final String STARTING_PROCESS = "Starting Process";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";

    @Autowired
    private CharacteristicService characteristicService;

    @PostMapping
    public ResponseEntity<Object> addCharacteristic(@RequestBody CharacteristicDto characteristicDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(characteristicService.entityToDto(characteristicService.addCharacteristic(characteristicDto)));
        } catch (MissingValuesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @GetMapping(value = {"/{idCharacteristic}"})
    public ResponseEntity<Object> findCharacteristicById(@PathVariable Long idCharacteristic) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(characteristicService.findCharacteristicById(idCharacteristic));
        } catch (NotInDataBaseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @PutMapping(value = {"/{idCharacteristic}"})
    public ResponseEntity<Object> updateCharacteristic(@PathVariable Long idCharacteristic, @RequestBody CharacteristicDto characteristicDto) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(characteristicService.updateCharacteristic(characteristicDto, idCharacteristic));
        } catch (NotInDataBaseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (MissingValuesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @DeleteMapping("/deleteCharacteristic/{idCharacteristic}")
    public ResponseEntity<String> deleteCharacteristic(@PathVariable Long idCharacteristic) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            characteristicService.deleteCharacteristicById(idCharacteristic);
            return ResponseEntity.status(HttpStatus.OK).body(String.format(CHARACTERISTIC_DELETED_BY_ID, idCharacteristic));

        } catch (NotInDataBaseException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Object> findAllCharacteristics() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        try {
            return ResponseEntity.ok(characteristicService.listAllCharacteristics());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }
}
