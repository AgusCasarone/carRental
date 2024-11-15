package com.example.carRental.service;

import com.example.carRental.dto.UsagePolicyDto;
import com.example.carRental.entity.UsagePolicy;
import com.example.carRental.exception.MissingValuesException;
import com.example.carRental.exception.NotInDataBaseException;
import com.example.carRental.repository.UsagePolicyRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsagePolicyService {

    private static final Logger LOGGER = LogManager.getLogger(UsagePolicyService.class);

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

    private final UsagePolicyRepository usagePolicyRepository;

    public UsagePolicy addUsagePolicy(UsagePolicyDto usagePolicyDto) throws MissingValuesException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        UsagePolicy usagePolicyEntity = dtoToEntity(usagePolicyDto);

        checkIfMissingValues(usagePolicyEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        return usagePolicyRepository.save(usagePolicyEntity);
    }

    public UsagePolicy addUsagePolicy(UsagePolicy usagePolicy) throws MissingValuesException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        checkIfMissingValues(usagePolicy, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        return usagePolicyRepository.save(usagePolicy);
    }

    public UsagePolicy findUsagePolicyById(final Long idUsagePolicy) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idUsagePolicy, FIND_BY_ID);

        return usagePolicyRepository.findById(idUsagePolicy).
                orElseThrow(() -> new NotInDataBaseException(String.format(USAGE_POLICY_DOES_NOT_EXIST_BY_ID, idUsagePolicy)));

    }

    public UsagePolicy updateUsagePolicy(UsagePolicyDto updatedUsagePolicy, Long idUsagePolicyToUpdate) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idUsagePolicyToUpdate, UPDATE);

        UsagePolicy usagePolicyUpdated = copyAllNotNullValues(dtoToEntity(updatedUsagePolicy), findUsagePolicyById(idUsagePolicyToUpdate));
        usagePolicyUpdated.setIdUsagePolicy(idUsagePolicyToUpdate);

        LOGGER.info(UPDATE + PROCESS_FINISHED_SUCCESSFULLY);
        return usagePolicyRepository.save(usagePolicyUpdated);
    }

    public void deleteUsagePolicy(Long id) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(id, DELETE);

        LOGGER.info(String.format(USAGE_POLICY_DELETED_BY_ID, id));
        usagePolicyRepository.deleteById(id);
    }

    public List<UsagePolicy> listAllUsagePolicies() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return usagePolicyRepository.findAll();
    }

    public List<UsagePolicy> findAllById(List<Long> idUsagePoliciesList) throws NotInDataBaseException {

        try {
            idUsagePoliciesList.forEach(idUsagePolicy -> {
                try {
                    existsById(idUsagePolicy, FIND_ALL_BY_ID);
                } catch (NotInDataBaseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException notInDataBaseException) {
            LOGGER.error(FIND_ALL_BY_ID + FAILED_BECAUSE + USAGE_POLICY_DOES_NOT_EXIST_BY_ID);
            throw new NotInDataBaseException(FIND_ALL_BY_ID + FAILED_BECAUSE + USAGE_POLICY_DOES_NOT_EXIST_BY_ID);
        }

        return usagePolicyRepository.findAllById(idUsagePoliciesList);

    }

    public void existsById(Long idUsagePolicy, String desiredAction) throws NotInDataBaseException {

        if (!usagePolicyRepository.existsById(idUsagePolicy)) {
            LOGGER.error(String.format(desiredAction + FAILED_BECAUSE + USAGE_POLICY_DOES_NOT_EXIST_BY_ID, idUsagePolicy));
            throw new NotInDataBaseException(String.format(desiredAction + FAILED_BECAUSE + USAGE_POLICY_DOES_NOT_EXIST_BY_ID, idUsagePolicy));
        }

    }


    public UsagePolicy dtoToEntity(UsagePolicyDto usagePolicyDto) {

        UsagePolicy usagePolicyEntity = new UsagePolicy();

        usagePolicyEntity.setIdUsagePolicy(usagePolicyDto.getIdUsagePolicy());
        usagePolicyEntity.setDescription(usagePolicyDto.getDescription());
        usagePolicyEntity.setUsagePolicyType(usagePolicyDto.getUsagePolicyType());

        return usagePolicyEntity;
    }

    public UsagePolicyDto entityToDto(UsagePolicy usagePolicyEntity) {

        UsagePolicyDto usagePolicyDto = new UsagePolicyDto();

        usagePolicyDto.setIdUsagePolicy(usagePolicyEntity.getIdUsagePolicy());
        usagePolicyDto.setDescription(usagePolicyEntity.getDescription());
        usagePolicyDto.setUsagePolicyType(usagePolicyEntity.getUsagePolicyType());

        return usagePolicyDto;
    }

    public void checkIfMissingValues(UsagePolicy usagePolicyEntity, String desiredAction) throws MissingValuesException {

        if (usagePolicyEntity.getDescription() == null || usagePolicyEntity.getDescription().isBlank()
                || usagePolicyEntity.getUsagePolicyType() == null) {
            LOGGER.error(String.format(desiredAction + FAILED_BECAUSE + USAGE_POLICY_DOES_NOT_EXIST_BY_ID, usagePolicyEntity.getIdUsagePolicy()));
            throw new MissingValuesException(desiredAction + FAILED_BECAUSE + MISSING_VALUES);
        }
    }

    private UsagePolicy copyAllNotNullValues(UsagePolicy usagePolicyWithUpdates, UsagePolicy usagePolicyToUpdate) {

        if (usagePolicyWithUpdates.getUsagePolicyType() != null)
            usagePolicyToUpdate.setUsagePolicyType(usagePolicyWithUpdates.getUsagePolicyType());
        if (usagePolicyWithUpdates.getDescription() != null && !usagePolicyWithUpdates.getDescription().isBlank())
            usagePolicyToUpdate.setDescription(usagePolicyWithUpdates.getDescription());

        return usagePolicyToUpdate;
    }
}
