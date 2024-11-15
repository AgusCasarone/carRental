package com.example.carRental.service;

import com.example.carRental.dto.VehicleTypeDto;
import com.example.carRental.entity.VehicleType;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.repository.VehicleTypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleTypeService {

    private static final Logger LOGGER = LogManager.getLogger(VehicleService.class);

    private static final String CREATE = "Create - ";
    private static final String FIND_BY_ID = "Find by ID - ";
    private static final String UPDATE = "Update - ";
    private static final String DELETE = "Delete - ";
    private static final String LIST_ALL = "List all - ";
    private static final String FIND_ALL_BY_ID = "Find All by ID - ";
    private static final String FAILED_BECAUSE = "Failed because - ";
    private static final String MISSING_VALUES = "There are missing values ";
    private static final String VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID = "Vehicle Type with ID %s does not exist ";
    private static final String VEHICLE_TYPE_DELETED_BY_ID = "Vehicle Type with ID %s deleted successfully.";
    private static final String STARTING_PROCESS = "Starting Process - ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;


    public VehicleType addVehicle(VehicleTypeDto vehicleTypeDto) throws MissingValuesException, NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        VehicleType vehicleTypeEntity = dtoToEntity(vehicleTypeDto);

        checkIfMissingValues(vehicleTypeEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        return vehicleTypeRepository.save(vehicleTypeEntity);
    }

    public VehicleType findVehicleTypeById(Long idVehicleType) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idVehicleType, FIND_BY_ID);

        return vehicleTypeRepository.findById(idVehicleType).
                orElseThrow(() -> new NotInDataBaseException(String.format(VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID, idVehicleType)));

    }

    public VehicleType updateVehicleTypeById(VehicleTypeDto updatedVehicleType, Long idVehicleTypeToUpdate) throws NotInDataBaseException, MissingValuesException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idVehicleTypeToUpdate, UPDATE);

        VehicleType vehicleTypeUpdated = copyAllNotNullValues(dtoToEntity(updatedVehicleType), findVehicleTypeById(idVehicleTypeToUpdate));
        vehicleTypeUpdated.setIdVehicleType(idVehicleTypeToUpdate);

        LOGGER.info(UPDATE + PROCESS_FINISHED_SUCCESSFULLY);
        return vehicleTypeRepository.save(vehicleTypeUpdated);
    }

    public void deleteVehicleType(Long idVehicleType) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(idVehicleType, DELETE);

        LOGGER.info(String.format(VEHICLE_TYPE_DELETED_BY_ID, idVehicleType));
        vehicleTypeRepository.deleteById(idVehicleType);
    }
    public List<VehicleType> listAllVehicleTypes() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return vehicleTypeRepository.findAll();
    }

    public List<VehicleType> findAllById(List<Long> idVehicleTypesList) throws NotInDataBaseException{

        LOGGER.info(STARTING_PROCESS + FIND_ALL_BY_ID);
        try {
            idVehicleTypesList.forEach(idVehicleType -> {
                try {
                    existsById(idVehicleType, FIND_ALL_BY_ID);
                } catch (NotInDataBaseException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (RuntimeException notInDataBaseException) {
            LOGGER.error(FIND_ALL_BY_ID + FAILED_BECAUSE + VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID);
            throw new NotInDataBaseException(FIND_ALL_BY_ID + FAILED_BECAUSE + VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID);
        }

        return vehicleTypeRepository.findAllById(idVehicleTypesList);

    }

    public VehicleType dtoToEntity(VehicleTypeDto vehicleTypeDto) throws NotInDataBaseException, MissingValuesException {

        VehicleType vehicleTypeEntity = new VehicleType();

        vehicleTypeEntity.setIdVehicleType(vehicleTypeDto.getIdVehicleType());
        vehicleTypeEntity.setTitle(vehicleTypeDto.getTitle());
        vehicleTypeEntity.setDetails(vehicleTypeDto.getDetails());

        return vehicleTypeEntity;
    }

    public VehicleTypeDto entityToDto(VehicleType vehicleTypeEntity) {

        VehicleTypeDto vehicleTypeDto = new VehicleTypeDto();

        vehicleTypeDto.setIdVehicleType(vehicleTypeEntity.getIdVehicleType());
        vehicleTypeDto.setTitle(vehicleTypeEntity.getTitle());
        vehicleTypeDto.setDetails(vehicleTypeEntity.getDetails());

        return vehicleTypeDto;
    }

    public void checkIfMissingValues(VehicleType vehicleType, String process) throws MissingValuesException {

        if ((vehicleType.getTitle() == null || vehicleType.getTitle().isBlank())
                || (vehicleType.getDetails() == null || vehicleType.getDetails().isBlank())) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES);
        }
    }

    public void existsById(Long vehicleTypeId, String desiredAction) throws NotInDataBaseException {

        if (!vehicleTypeRepository.existsById(vehicleTypeId)) {
            LOGGER.error(String.format(desiredAction + FAILED_BECAUSE + VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID, vehicleTypeId));
            throw new NotInDataBaseException(String.format(desiredAction + FAILED_BECAUSE + VEHICLE_TYPE_DOES_NOT_EXIST_BY_ID, vehicleTypeId));
        }
    }


    public VehicleType copyAllNotNullValues(VehicleType vehicleTypeWithUpdates, VehicleType vehicleTypeToUpdate) {

        if(vehicleTypeWithUpdates.getTitle() != null && !vehicleTypeWithUpdates.getTitle().isBlank())
            vehicleTypeToUpdate.setTitle(vehicleTypeWithUpdates.getTitle());
        if(vehicleTypeWithUpdates.getDetails() != null && !vehicleTypeWithUpdates.getDetails().isBlank())
            vehicleTypeToUpdate.setDetails(vehicleTypeWithUpdates.getDetails());

        return vehicleTypeToUpdate;

    }
}
