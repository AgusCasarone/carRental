package com.example.carRental.controller;

import com.example.carRental.dto.UsagePolicyDto;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.service.UsagePolicyService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/usagePolicy")
@CrossOrigin(origins = "*")
public class UsagePolicyController {

    private static final Logger LOGGER = LogManager.getLogger(UsagePolicyController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String LIST_ALL = "List all - ";
    private static final String FIND_ALL_BY_ID = "Find All by ID - ";
    private static final String FAILED_BECAUSE = "Failed because - ";
    private static final String MISSING_VALUES = "There are missing values ";
    private static final String USAGE_POLICY_DOES_NOT_EXIST_BY_ID = "Usage Policy with ID %s does not exist ";
    private static final String USAGE_POLICY_DELETED_BY_ID = "Usage Policy with ID %s deleted successfully.";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";

    private final UsagePolicyService usagePolicyService;


    @PostMapping
    public ResponseEntity<Object> addUsagePolicy(@RequestBody UsagePolicyDto usagePolicyDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(usagePolicyService.addUsagePolicy(usagePolicyDto));
        } catch (MissingValuesException e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + MISSING_VALUES + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (PROCESS_ABORTED_DUE_TO + MISSING_VALUES);
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }

    }

    @GetMapping(value = {"/{idUsagePolicy}"})
    public ResponseEntity<Object> findUsagePolicyById(@PathVariable Long idUsagePolicy) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(usagePolicyService.findUsagePolicyById(idUsagePolicy));
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

    @PutMapping(value = {"/{idUsagePolicy}"})
    public ResponseEntity<Object> updateUsagePolicyById(@PathVariable Long idUsagePolicy, @RequestBody UsagePolicyDto updatedUsagePolicy) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(usagePolicyService.updateUsagePolicy(updatedUsagePolicy, idUsagePolicy));
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

    @DeleteMapping(value =  "delete/{idUsagePolicy}")
    public ResponseEntity<Object> deleteUsagePolicy(@PathVariable Long idUsagePolicy) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            usagePolicyService.deleteUsagePolicy(idUsagePolicy);
            LOGGER.info(DELETE + PROCESS_FINISHED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK).body
                    (String.format(USAGE_POLICY_DELETED_BY_ID, idUsagePolicy));

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
    public ResponseEntity<Object> listAllUsagePolicies() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        try {
            return ResponseEntity.ok(
                    usagePolicyService.listAllUsagePolicies());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

}
