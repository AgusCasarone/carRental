package com.example.carRental.service;

import com.example.carRental.dto.UsersDto;
import com.example.carRental.entity.Users;
import com.example.carRental.exception.AlreadyExistsInDataBaseException;
import com.example.carRental.exception.InvalidValuesException;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.repository.RolRepository;
import com.example.carRental.repository.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger LOGGER = LogManager.getLogger(UsersService.class);

    private static final String CREATE = "Create - ";
    private static final String CHECK_VALUES_FORMAT = "Check values format - ";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FAILED_BECAUSE = "Failed because - ";
    private static final String INVALID_FORMAT = "Invalid %s format";
    private static final String MISSING_VALUES = "There are missing values ";
    private static final String USER_DOES_NOT_EXIST_BY_EMAIL = "User with email %s does not exist ";
    private static final String USER_ALREADY_EXIST_BY_EMAIL = "User with email %s already exist ";
    private static final String USER_DELETED_BY_EMAIL = "User with email %s deleted successfully.";
    private static final String STARTING_PROCESS = "Starting Process - ";

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RolRepository rolRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public Users saveUser(UsersDto usersDto) throws MissingValuesException, InvalidValuesException,
            AlreadyExistsInDataBaseException, NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        Users usersEntity = dtoToEntity(usersDto);

        validateNewUser(usersEntity);

        usersEntity.setPassword(passwordEncoder.encode(usersDto.getPassword()));
        if (usersDto.getIdRoles() != null && rolRepository.existsById(usersDto.getIdRoles())) {
                usersEntity.setRoles(rolRepository.findById(usersDto.getIdRoles()).orElseThrow(
                    () -> new NotInDataBaseException(String.format("The role %s does not exist", usersDto.getIdRoles()))));
        } else {
            usersEntity.setRoles(rolRepository.findById(3L).orElseThrow(
                    () -> new NotInDataBaseException(String.format("The role %s does not exist", 3L))));
        }

        return usersRepository.save(usersEntity);
    }

    public Users ascendToAdmin(UsersDto usersDto) {

        Users users = usersRepository.findByEmailUser(usersDto.getEmail());

        users.setRoles(rolRepository.findById(2L).orElseThrow());

        return usersRepository.save(users);
    }

    public Optional<Users> findUsersById(Long id){
        return usersRepository.findById(id);
    }

    public List<Users> findAllUsers(){
        return usersRepository.findAll();}

    public Users findByEmailUser(String email){
        return usersRepository.findByEmailUser(email);}

    public Users dtoToEntity(UsersDto usersDto) {

        Users usersEntity = new Users();

        usersEntity.setIdUsers(usersDto.getIdUsers());
        usersEntity.setName(usersDto.getFirstName());
        usersEntity.setLastname(usersDto.getLastName());
        usersEntity.setEmail(usersDto.getEmail());
        usersEntity.setPassword(usersDto.getPassword());

        return usersEntity;

    }

    public void checkIfMissingValues(Users users, String process) throws MissingValuesException {

        List<String> missingValues = new ArrayList<>();
        if (users.getName() == null || users.getName().isBlank()) {
            missingValues.add("name");
        }
        if (users.getLastname() == null || users.getLastname().isBlank()) {
            missingValues.add("last name");
        }
        if (users.getEmail() == null || users.getEmail().isBlank()) {
            missingValues.add("email");
        }
        if (users.getPassword() == null || users.getPassword().isBlank()) {
            missingValues.add("password");
        }

        if (!missingValues.isEmpty()) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES + missingValues);
        }
    }

    public void checkUserAndPasswordFormat(Users users) throws InvalidValuesException {

        LOGGER.info(STARTING_PROCESS + CHECK_VALUES_FORMAT);

        List<String> invalidValues = new ArrayList<>();

        if (users.getPassword().length() < 6) {
            invalidValues.add(PASSWORD);
        }
        if (!users.getEmail().matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*")) {
            invalidValues.add(EMAIL);
        }

        if (!invalidValues.isEmpty()) {
            throw new InvalidValuesException(String.format(CHECK_VALUES_FORMAT + FAILED_BECAUSE + INVALID_FORMAT, invalidValues));
        }

    }

    public void validateNewUser(Users users) throws MissingValuesException, InvalidValuesException, AlreadyExistsInDataBaseException {

        checkIfMissingValues(users, CREATE);

        checkUserAndPasswordFormat(users);

        if (usersRepository.findByEmailUser(users.getEmail()) != null) {
            throw new AlreadyExistsInDataBaseException(
                    String.format(CREATE + FAILED_BECAUSE + USER_ALREADY_EXIST_BY_EMAIL, users.getEmail()));
        }
    }
}
