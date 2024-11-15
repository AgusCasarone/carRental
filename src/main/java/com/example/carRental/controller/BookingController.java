package com.example.carRental.controller;

import com.example.carRental.dto.BookingDto;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/booking")
@CrossOrigin("*")
public class BookingController {
    private static final Logger LOGGER = LogManager.getLogger(BookingController.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String BOOKING_DELETED_BY_ID = "Booking with id %s deleted";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String PROCESS_ABORTED_DUE_TO = "Process aborted due to - ";
    private static final String INTERNAL_SERVER_ERRORS = "Internal server errors. Please, contact developers to report this.";

    @Autowired
    BookingService bookingService;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody BookingDto bookingDto) {

        LOGGER.info(STARTING_PROCESS + CREATE);

        try {
            return ResponseEntity.ok(bookingService.entityToDto(bookingService.addBooking(bookingDto)));
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

    @GetMapping(value = {"/{idBooking}"})
    public ResponseEntity<Object> findBookingById(@PathVariable Long idBooking) {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        try {
            return ResponseEntity.ok(bookingService.findBookingById(idBooking));
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

    @PutMapping(value = {"/{idBooking}"})
    public ResponseEntity<Object> updateBookingById(@PathVariable Long idBooking, @RequestBody BookingDto bookingDto) {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        try {
            return ResponseEntity.ok(bookingService.updateBooking(bookingDto, idBooking));
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

    @DeleteMapping(value =  "delete/{idBooking}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long idBooking) {

        LOGGER.info(STARTING_PROCESS + DELETE);

        try {
            bookingService.deleteBookingById(idBooking);
            LOGGER.info(DELETE + PROCESS_FINISHED_SUCCESSFULLY);
            return ResponseEntity.status(HttpStatus.OK).body
                    (String.format(BOOKING_DELETED_BY_ID, idBooking));

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
    public ResponseEntity<Object> listAllBookings(){
        try {
            return ResponseEntity.ok(
                    bookingService.listAllBookings());
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @GetMapping(value = "/findAllByProductId/{idVehicle}")
    public ResponseEntity<Object> findAllByProductId(@PathVariable Long idVehicle) {
        try {
            return ResponseEntity.ok(
                    bookingService.findAllByProductId(idVehicle));
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }

    @GetMapping(value = "/findAllByUserId/{userId}")
    public ResponseEntity<Object> findAllByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(
                    bookingService.findAllByUserId(userId));
        } catch (Exception e) {
            LOGGER.error(PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body
                    (PROCESS_ABORTED_DUE_TO + INTERNAL_SERVER_ERRORS);
        }
    }
}
