package com.example.carRental.controller;

import com.example.carRental.dto.ProvinceDto;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.service.ProvinceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/province")
@CrossOrigin("*")
public class ProvinceController {
    private static final Logger LOGGER = LogManager.getLogger(ProvinceController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String PROVINCE_DELETED_BY_ID = "Province with id %s deleted";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";

    @Autowired
    ProvinceService provinceService;

    @PostMapping
    public ResponseEntity<Object> addProvince(@RequestBody ProvinceDto provinceDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(provinceService.entityToDto(provinceService.addProvince(provinceDto)));
        } catch (MissingValuesException | WronglyPopulatedListsException e) {
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

    @GetMapping(value = {"/{idProvince}"})
    public ResponseEntity<Object> findProvinceById(@PathVariable Long idProvince) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(provinceService.findProvinceById(idProvince));
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

    @PutMapping(value = {"/{idProvince}"})
    public ResponseEntity<Object> updateProvinceById(@PathVariable Long idProvince, @RequestBody ProvinceDto provinceDto) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(provinceService.updateProvince(provinceDto, idProvince));
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

    @DeleteMapping(value =  "delete/{idProvince}")
    public ResponseEntity<String> deleteProvince(@PathVariable Long idProvince) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            provinceService.deleteProvinceById(idProvince);
            LOGGER.info(DELETE + PROCESS_FINISHED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK).body
                    (String.format(PROVINCE_DELETED_BY_ID, idProvince));

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
    public ResponseEntity<Object> listAllProvinces(){
        try {
            return ResponseEntity.ok(
                    provinceService.listAllProvinces());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }
}
