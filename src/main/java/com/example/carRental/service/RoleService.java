package com.example.carRental.service;

import com.example.carRental.dto.RoleDto;
import com.example.carRental.entity.Roles;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.exception.WronglyPopulatedListsException;
import com.example.carRental.repository.RolRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private static final Logger LOGGER = LogManager.getLogger(RoleService.class);

    private static final String CREATE = "Create: ";
    private static final String NEW_ROLE_CREATED = "New Role created.";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";
    private static final String ROLE_UPDATED_BY_ID = "Role updated by id %s";
    private static final String ROLE_DELETED_BY_ID = "Role deleted by id %s";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "Missing values: ";
    private static final String ROLE_DOES_NOT_EXIST_BY_ID = "Role with id %s does not exist";

    @Autowired
    RolRepository roleRepository;

    @Transactional
    public Roles addRole(RoleDto roleDto) throws MissingValuesException, NotInDataBaseException, WronglyPopulatedListsException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        Roles roleEntity = dtoToEntity(roleDto);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        LOGGER.info(NEW_ROLE_CREATED);
        return roleRepository.save(roleEntity);
    }
    
    public List<Roles> findAllRoles() {
        return roleRepository.findAll();
    }

    public boolean existsById(Long idRole) {
        return roleRepository.existsById(idRole);
    }

    public Roles dtoToEntity(RoleDto roleDto) {

        Roles roleEntity = new Roles();

        roleEntity.setName(roleDto.getName());

        return roleEntity;
    }

    public RoleDto entityToDto(Roles roleEntity) {

        RoleDto roleDto = new RoleDto();

        roleDto.setIdRoles(roleEntity.getIdRoles());
        roleDto.setName(roleEntity.getName());

        return roleDto;
    }
}
