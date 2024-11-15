package com.example.carRental.service;

import com.example.carRental.dto.BookingDto;
import com.example.carRental.entity.Booking;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.repository.BookingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    private static final Logger LOGGER = LogManager.getLogger(BookingService.class);

    private static final String CREATE = "Create: ";
    private static final String NEW_BOOKING_CREATED = "New Booking created.";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";

    private static final String LIST_ALL_BY_PRODUCT_ID = "List all by product id %s";
    private static final String LIST_ALL_BY_USER_ID = "List all by user id %s";

    private static final String BOOKING_UPDATED_BY_ID = "Booking updated by id %s";
    private static final String BOOKING_DELETED_BY_ID = "Booking deleted by id %s";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "Missing values: ";
    private static final String BOOKING_DOES_NOT_EXIST_BY_ID = "Booking with id %s does not exist";

    private static final String BOOKING_PAGINATED_LIST = "Booking paginated list: ";

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    VehicleService vehicleService;

    public Booking addBooking(BookingDto bookingDto) throws MissingValuesException, NotInDataBaseException, WronglyPopulatedListsException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        Booking bookingEntity = dtoToEntity(bookingDto);

        System.out.println(bookingDto.getUserId());

        checkIfMissingValues(bookingEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        LOGGER.info(NEW_BOOKING_CREATED);
        return bookingRepository.save(bookingEntity);
    }

    public Booking findBookingById(Long idBooking) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idBooking, FIND_BY_ID);
        return bookingRepository.findById(idBooking).
                orElseThrow(() -> new NotInDataBaseException(String.format(BOOKING_DOES_NOT_EXIST_BY_ID, idBooking)));
    }

    public Booking updateBooking(BookingDto updatedBooking, Long idBookingToUpdate) throws NotInDataBaseException, MissingValuesException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idBookingToUpdate, UPDATE);

        Booking bookingOriginal = findBookingById(idBookingToUpdate);

        if (updatedBooking.getVehicleId() != null) {
            bookingOriginal.setVehicle(vehicleService.findVehicleById(updatedBooking.getVehicleId()));
        }

        if (updatedBooking.getStartDate() != null) {
            bookingOriginal.setStartDate(updatedBooking.getStartDate());
        }

        if (updatedBooking.getEndDate() != null) {
            bookingOriginal.setEndDate(updatedBooking.getEndDate());
        }

        if(updatedBooking.getUserId() != null) {
            bookingOriginal.setUserId(updatedBooking.getUserId());
        }

        bookingOriginal.setId(idBookingToUpdate);

        LOGGER.info(String.format(BOOKING_UPDATED_BY_ID, idBookingToUpdate));
        return bookingRepository.save(bookingOriginal);
    }

    public void deleteBookingById(Long idBooking) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(idBooking, DELETE);

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + DELETE);
        bookingRepository.deleteById(idBooking);
    }

    public List<Booking> listAllBookings() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return bookingRepository.findAll();
    }

    public List<List<Booking>> generatePaginationArray(List<Booking> allBookings) {

        LOGGER.info(STARTING_PROCESS + BOOKING_PAGINATED_LIST);

        List<List<Booking>> bookingArrays = new ArrayList<>();
        List<Booking> currentBookingArray = new ArrayList<>();

        for (int i = 0; i < allBookings.size(); i++) {
            Booking booking = allBookings.get(i);
            currentBookingArray.add(booking);

            if (currentBookingArray.size() == 10 || i == allBookings.size()-1) {
                bookingArrays.add(currentBookingArray);
                currentBookingArray = new ArrayList<>();
            }
        }

        return bookingArrays;
    }

    public List<Booking> findAllByProductId(Long idVehicle) {
        LOGGER.info(STARTING_PROCESS + LIST_ALL_BY_PRODUCT_ID);

        return bookingRepository.findAllByProductId(idVehicle);
    }

    public List<List<Booking>> findAllByUserId(Long userId) {
        LOGGER.info(STARTING_PROCESS + LIST_ALL_BY_USER_ID);
        List<Booking> allBookings = bookingRepository.findAllByUserId(userId);

        return generatePaginationArray(allBookings);
    }

    public Booking dtoToEntity(BookingDto bookingDto) throws NotInDataBaseException {

        Booking bookingEntity = new Booking();

        if (bookingDto.getVehicleId() != null) {
            bookingEntity.setVehicle(vehicleService.findVehicleById(bookingDto.getVehicleId()));
        }

        if (bookingDto.getStartDate() != null) {
            bookingEntity.setStartDate(bookingDto.getStartDate());
        }

        if (bookingDto.getEndDate() != null) {
            bookingEntity.setEndDate(bookingDto.getEndDate());
        }

        if(bookingDto.getUserId() != null) {
            bookingEntity.setUserId(bookingDto.getUserId());
        }

        bookingEntity.setId(bookingDto.getId());

        return bookingEntity;
    }

    public BookingDto entityToDto(Booking bookingEntity) throws NotInDataBaseException {

        BookingDto bookingDto = new BookingDto();

        if (bookingEntity.getVehicle() != null) {
            bookingDto.setVehicleId(vehicleService.findVehicleById(bookingEntity.getVehicle().getIdVehicle()).getIdVehicle());
        }

        if (bookingEntity.getStartDate() != null) {
            bookingDto.setStartDate(bookingEntity.getStartDate());
        }

        if (bookingEntity.getEndDate() != null) {
            bookingDto.setEndDate(bookingEntity.getEndDate());
        }

        if(bookingEntity.getUserId() != null) {
            bookingDto.setUserId((bookingEntity.getUserId()));
        }

        bookingDto.setId(bookingEntity.getId());

        return bookingDto;
    }

    public void checkIfMissingValues(Booking booking, String process) throws MissingValuesException, NotInDataBaseException {

        List<String> missingValues = new ArrayList<>();

        if (booking.getVehicle() == null) {
            vehicleService.existsById(booking.getVehicle().getIdVehicle(), process);
            missingValues.add("vehicle");
        }

        if (booking.getStartDate() == null) {
            missingValues.add("start date");
        }

        if (booking.getEndDate() == null) {
            missingValues.add("end date");
        }

        System.out.println(booking.getUserId());

        if(booking.getUserId() == null) {
            missingValues.add("user id");
        }

        if (!missingValues.isEmpty()) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES + missingValues);
        }
    }

    public void existsById(Long bookingId, String process) throws NotInDataBaseException {

        if (!bookingRepository.existsById(bookingId)) {
            LOGGER.error(String.format(process, FAILED_BECAUSE, BOOKING_DOES_NOT_EXIST_BY_ID, bookingId));
            throw new NotInDataBaseException(String.format(process + FAILED_BECAUSE + BOOKING_DELETED_BY_ID, bookingId));
        }
    }
}
