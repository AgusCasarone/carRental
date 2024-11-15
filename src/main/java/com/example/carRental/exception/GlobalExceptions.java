package com.example.carRental.exception;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {

    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptions.class);

    private static final String RESOURCE_NOT_FOUND = "The resource you are trying to access was not found.";
    private static final String MISSING_VALUES = "The request could not finish due to missing values.";
    private static final String ALREADY_EXISTS_IN_DATA_BASE = "The request could not finish because this same resource already exists in the Data Base.";
    private static final String ERROR_UPLOADING_FILE = "Error uploading file.";
    private static final String WRONG_QUANTITY_OF_SOME_ATTRIBUTE = "You need to evaluate how many of each attribute you sent and how many were actually required";
    private static final String INVALID_VALUES = "Invalid values";

    @ExceptionHandler({NotInDataBaseException.class})
    public ResponseEntity<String> processExceptionNotInDataBase(NotInDataBaseException e) {
        LOGGER.error(RESOURCE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({MissingValuesException.class})
    public ResponseEntity<String> processExceptionMissingValues(MissingValuesException e) {
        LOGGER.error(MISSING_VALUES);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({AlreadyExistsInDataBaseException.class})
    public ResponseEntity<String> processExceptionAlreadyExistsInDataBase(AlreadyExistsInDataBaseException e) {
        LOGGER.error(ALREADY_EXISTS_IN_DATA_BASE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({WronglyPopulatedListsException.class})
    public ResponseEntity<String> processWronglyPopulatedLists(AlreadyExistsInDataBaseException e) {
        LOGGER.error(WRONG_QUANTITY_OF_SOME_ATTRIBUTE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({InvalidValuesException.class})
    public ResponseEntity<String> processInvalidValues(InvalidValuesException e) {
        LOGGER.error(INVALID_VALUES);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({InvalidUsagePolicyTypeException.class})
    public ResponseEntity<String> processInvalidUsagePolicyTypeException(InvalidUsagePolicyTypeException e) {
        LOGGER.error(INVALID_VALUES);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ErrorUploadingFileException.class})
    public ResponseEntity<String> processExceptionErrorUploadingFile(ErrorUploadingFileException e) {
        LOGGER.error(ERROR_UPLOADING_FILE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}