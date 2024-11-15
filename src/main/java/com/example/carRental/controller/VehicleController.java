package com.example.carRental.controller;

import com.example.carRental.dto.VehicleDto;
import com.example.carRental.exception.*;
import com.example.carRental.service.VehicleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicle")
@CrossOrigin("*")
public class VehicleController {

    private static final Logger LOGGER = LogManager.getLogger(VehicleController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String FILTER_BY = "Filter by ";
    private static final String CATEGORY = "Category";
    private static final String VEHICLE_DELETED_BY_ID = "Vehicle with id %s deleted";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String MISSING_VALUES = "Missing values in the request";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";


    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Object> addVehicle(@RequestBody VehicleDto vehicleDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(vehicleService.entityToDto(vehicleService.addVehicle(vehicleDto)));
        } catch (MissingValuesException | WronglyPopulatedListsException |
                 AlreadyExistsInDataBaseException | InvalidValuesException | InvalidUsagePolicyTypeException e) {
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

    @GetMapping(value = {"/{idVehicle}"})
    public ResponseEntity<Object> findVehicleById(@PathVariable Long idVehicle) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(vehicleService.findVehicleById(idVehicle));
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

    @PutMapping(value = {"/{idVehicle}"})
    public ResponseEntity<Object> updateVehicleById(@PathVariable Long idVehicle, @RequestBody VehicleDto updatedVehicle) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(vehicleService.updateVehicleById(updatedVehicle, idVehicle));
        } catch (NotInDataBaseException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (MissingValuesException | WronglyPopulatedListsException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @DeleteMapping(value =  "delete/{idVehicle}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long idVehicle) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            vehicleService.deleteVehicle(idVehicle);
            LOGGER.info(DELETE + PROCESS_FINISHED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK).body
                    (String.format(VEHICLE_DELETED_BY_ID, idVehicle));

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
    public ResponseEntity<Object> listAllVehicles(){
        try {
            return ResponseEntity.ok(
                    vehicleService.listAllVehicles());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @GetMapping(value = "/random-list")
    public ResponseEntity<Object> vehiclesRandomList() {
        try {
            return ResponseEntity.ok(
                    vehicleService.vehicleRandomList()
            );
        }  catch (NotInDataBaseException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (e.getMessage());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }


    @GetMapping(value = "/category")
    public ResponseEntity<Object> filterVehiclesByCategory(@RequestParam String vehicleTypeId) {

        LOGGER.info(STARTING_PROCESS + FILTER_BY + CATEGORY);

        try {
            return ResponseEntity.ok(vehicleService.filterVehiclesByCategory(vehicleTypeId));
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

    @GetMapping("/filterByCityName/{name}")
    public ResponseEntity<Object> findForCity(@PathVariable String name) {
        try {
            return ResponseEntity.ok(vehicleService.filterByCityName(name));
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

    @GetMapping("/filterByDate")
    public ResponseEntity<Object> findByDate(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(vehicleService.filterByDate(start, end));
    }

    @GetMapping("/filterByCityDate/{city}")
    public ResponseEntity<Object> findByCityDate(@PathVariable String city, @RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(vehicleService.filterByCityDate(city, start, end));
    }

    @GetMapping("/findByPlate")
    public ResponseEntity<Object> findByPlate(@RequestParam String plate) {
        try {
            return ResponseEntity.ok(vehicleService.findByPlate(plate));
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
}
