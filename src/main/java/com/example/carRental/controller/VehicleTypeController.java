package com.example.carRental.controller;

import com.example.carRental.dto.VehicleTypeDto;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.service.VehicleTypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicleType")
@CrossOrigin("*")
public class VehicleTypeController {

    private static final Logger LOGGER = LogManager.getLogger(VehicleController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String LIST_ALL = "List All - ";
    private static final String VEHICLE_TYPE_DELETED_BY_ID = "Vehicle Type with ID %s was deleted";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";


    @Autowired
    private VehicleTypeService vehicleTypeService;

    @PostMapping
    public ResponseEntity<Object> addVehicleType(@RequestBody VehicleTypeDto vehicleTypeDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(vehicleTypeService.entityToDto(vehicleTypeService.addVehicle(vehicleTypeDto)));
        } catch (MissingValuesException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
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

    @GetMapping(value = {"/{idVehicleType}"})
    public ResponseEntity<Object> findVehicleTypeById(@PathVariable Long idVehicleType) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(vehicleTypeService.findVehicleTypeById(idVehicleType));
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

    @PutMapping(value = {"/{idVehicleType}"})
    public ResponseEntity<Object> updateVehicleTypeById(@PathVariable Long idVehicleType, @RequestBody VehicleTypeDto updatedVehicleType) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(vehicleTypeService.updateVehicleTypeById(updatedVehicleType, idVehicleType));
        } catch (NotInDataBaseException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (MissingValuesException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @DeleteMapping(value =  "delete/{idVehicleType}")
    public ResponseEntity<Object> deleteVehicleType(@PathVariable Long idVehicleType) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            vehicleTypeService.deleteVehicleType(idVehicleType);
            LOGGER.info(DELETE + PROCESS_FINISHED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK).body
                    (String.format(VEHICLE_TYPE_DELETED_BY_ID, idVehicleType));

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
    public ResponseEntity<Object> listAllVehicleTypes() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        try {
            return ResponseEntity.ok(
                    vehicleTypeService.listAllVehicleTypes());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }
}
