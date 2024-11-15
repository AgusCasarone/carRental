package com.example.carRental.controller;

import com.example.carRental.dto.UsersDto;
import com.example.carRental.entity.Users;
import com.example.carRental.exception.AlreadyExistsInDataBaseException;
import com.example.carRental.exception.InvalidValuesException;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.service.UsersService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private static final Logger LOGGER = LogManager.getLogger(UsersController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_EMAIL = "Find by email - ";
    private static final String UPDATE = "Update - ";
    private static final String ASCEND_USER = "Ascend User -";
    private static final String DELETE = "Delete - ";
    private static final String USER_DELETED_BY_EMAIL = "User with email %s deleted";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String USER_ALREADY_EXIST_BY_EMAIL = "User with email %s already exist ";

    private static final String MISSING_VALUES = "Missing values - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";

    @Autowired
    private UsersService usersService;

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody UsersDto usersDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(usersService.saveUser(usersDto));
        } catch (MissingValuesException e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + MISSING_VALUES + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        } catch (AlreadyExistsInDataBaseException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (String.format(PROCESS_ABORTED_DUE_TO + USER_ALREADY_EXIST_BY_EMAIL, usersDto.getEmail()));
        } catch (InvalidValuesException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    (e.getMessage());
        }catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @PutMapping("/ascendToAdmin")
    public ResponseEntity<Object> ascendToAdmin(@RequestBody UsersDto usersDto) {

        LOGGER.info(STARTING_PROCESS + ASCEND_USER);

        try {
            return ResponseEntity.ok(usersService.ascendToAdmin(usersDto));
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Users>> findAll() {
        return new ResponseEntity<>(usersService.findAllUsers(),null, HttpStatus.OK);
    }

    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(usersService.findByEmailUser(email));
    }
}
