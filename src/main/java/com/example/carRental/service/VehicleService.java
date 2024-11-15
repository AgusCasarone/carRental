package com.example.carRental.service;

import com.example.carRental.dto.VehicleDto;
import com.example.carRental.entity.Characteristic;
import com.example.carRental.entity.UsagePolicy;
import com.example.carRental.entity.UsagePolicyType;
import com.example.carRental.entity.Vehicle;
import com.example.carRental.exception.*;
import com.example.carRental.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VehicleService {

    private static final Logger LOGGER = LogManager.getLogger(VehicleService.class);

    private static final String CREATE = "Create: ";
    private static final String FIND_BY_ID = "Find by ID: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String LIST_ALL = "List all: ";
    private static final String FIND_ALL_BY_ID = "Find All by ID: ";
    private static final String VEHICLE_RANDOM_LIST = "Vehicle random list: ";
    private static final String FILTER_BY_CATEGORY = "Filter by Category - ";
    private static final String FILTER_BY_CITY_NAME = "Filter by City Name - ";
    private static final String VEHICLE_PAGINATED_LIST = "Vehicle paginated list: ";
    private static final String FAILED_BECAUSE = "Failed because: ";
    private static final String MISSING_VALUES = "There are missing values ";
    private static final String VEHICLE_DOES_NOT_EXIST_BY_ID = "Vehicle with ID %s does not exist ";
    private static final String VEHICLE_DOES_NOT_EXIST_BY_PLATE = "Vehicle with plate %s does not exist ";
    private static final String VEHICLE_ALREADY_EXIST_BY_PLATE = "Vehicle with plate %s already exists ";
    private static final String STARTING_PROCESS = "Starting Process ";
    private static final String PROCESS_FINISHED_SUCCESSFULLY = "Process finished successfully";
    private static final String VEHICLE_DELETED = "Vehicle deleted";
    private static final String VEHICLES_SHOULD_HAVE = "Vehicles should have - ";
    private static final String CHARACTERISTICS = "At least one Characteristic; ";
    private static final String USAGE_POLICIES = "Vehicles should have - At least one Usage Policy of each type: \n";
    private static final String NORMAS_DE_LA_CASA = "\nNORMAS_DE_LA_CASA: ";
    private static final String SALUD_Y_SEGURIDAD = "\nSALUD_Y_SEGURIDAD: ";
    private static final String POLITICA_DE_CANCELACION = "\nPOLITICA_DE_CANCELACION: ";
    private static final String WRONG_PLATE_LENGTH = "The plate should have between 6 and 11 alphanumeric characters.";

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CharacteristicService characteristicService;
    @Autowired
    private VehicleTypeService vehicleTypeService;
    @Autowired
    private CityService cityService;

    private final UsagePolicyService usagePolicyService;

    @Transactional
    public Vehicle addVehicle(VehicleDto vehicleDto) throws MissingValuesException, NotInDataBaseException, WronglyPopulatedListsException, AlreadyExistsInDataBaseException, InvalidValuesException {

        LOGGER.info(STARTING_PROCESS + CREATE);

        validatePlate(vehicleDto);

        Vehicle vehicleEntity = dtoToEntity(vehicleDto);

        checkIfMissingValues(vehicleEntity, CREATE);

        LOGGER.info(CREATE + PROCESS_FINISHED_SUCCESSFULLY);
        return vehicleRepository.save(vehicleEntity);
    }

    public Vehicle findVehicleById(Long idVehicle) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FIND_BY_ID);

        existsById(idVehicle, FIND_BY_ID);

        return vehicleRepository.findById(idVehicle).
                orElseThrow(() -> new NotInDataBaseException(String.format(VEHICLE_DOES_NOT_EXIST_BY_ID, idVehicle)));

    }

    @Transactional
    public Vehicle updateVehicleById(VehicleDto updatedVehicle, Long idVehicleToUpdate) throws NotInDataBaseException, MissingValuesException, WronglyPopulatedListsException {

        LOGGER.info(STARTING_PROCESS + UPDATE);

        existsById(idVehicleToUpdate, UPDATE);

        Vehicle vehicleUpdated = copyAllNotNullValues(dtoToEntity(updatedVehicle), findVehicleById(idVehicleToUpdate));
        vehicleUpdated.setIdVehicle(idVehicleToUpdate);

        LOGGER.info(UPDATE + PROCESS_FINISHED_SUCCESSFULLY);
        return vehicleRepository.save(vehicleUpdated);
    }

    @Transactional
    public void deleteVehicle(Long id) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + DELETE);

        existsById(id, DELETE);

        LOGGER.info(VEHICLE_DELETED);
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> listAllVehicles() {

        LOGGER.info(STARTING_PROCESS + LIST_ALL);

        return vehicleRepository.findAll();
    }

    public List<Vehicle> findAllById(List<Long> idVehiclesList) throws NotInDataBaseException{

        LOGGER.info(STARTING_PROCESS + FIND_ALL_BY_ID);
        try {
            idVehiclesList.forEach(idVehicle -> {
                try {
                    existsById(idVehicle, FIND_ALL_BY_ID);
                } catch (NotInDataBaseException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (RuntimeException notInDataBaseException) {
            LOGGER.error(FIND_ALL_BY_ID + FAILED_BECAUSE + VEHICLE_DOES_NOT_EXIST_BY_ID);
            throw new NotInDataBaseException(FIND_ALL_BY_ID + FAILED_BECAUSE + VEHICLE_DOES_NOT_EXIST_BY_ID);
        }

        return vehicleRepository.findAllById(idVehiclesList);

    }

    public List<List<Vehicle>> generatePaginationArray(List<Vehicle> allVehicles) {

        LOGGER.info(STARTING_PROCESS + VEHICLE_PAGINATED_LIST);

        List<List<Vehicle>> vehicleArrays = new ArrayList<>();
        List<Vehicle> currentVehicleArray = new ArrayList<>();

        for (int i = 0; i < allVehicles.size(); i++) {
            Vehicle vehicle = allVehicles.get(i);
            currentVehicleArray.add(vehicle);

            if (currentVehicleArray.size() == 10 || i == allVehicles.size()-1) {
                vehicleArrays.add(currentVehicleArray);
                currentVehicleArray = new ArrayList<>();
            }
        }

        return vehicleArrays;
    }

    public List<List<Vehicle>> vehicleRandomList() throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + VEHICLE_RANDOM_LIST);

        List<Vehicle> getAllVehicles = vehicleRepository.getRandomList();

        return generatePaginationArray(getAllVehicles);
    }

    public List<List<Vehicle>> filterVehiclesByCategory(String vehicleTypeIdString) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FILTER_BY_CATEGORY);

        Long vehicleTypeIdLong = Long.parseLong(vehicleTypeIdString);

        vehicleTypeService.existsById(vehicleTypeIdLong, FILTER_BY_CATEGORY);

        List<Vehicle> allVehicles = vehicleRepository.findAll();

        List<List<Vehicle>> vehicleArrays = new ArrayList<>();
        List<Vehicle> currentVehicleArray = new ArrayList<>();

        for (Vehicle vehicle : allVehicles) {
            if (vehicle.getVehicleType().getIdVehicleType().equals(vehicleTypeIdLong)) {
                if (currentVehicleArray.size() < 10) {
                    currentVehicleArray.add(vehicle);
                } else if (currentVehicleArray.size() == 10) {
                    vehicleArrays.add(currentVehicleArray);
                    currentVehicleArray = new ArrayList<>();
                    currentVehicleArray.add(vehicle);
                }
            }
        }

        if(currentVehicleArray.size() > 0) {
            vehicleArrays.add(currentVehicleArray);
        }

        return vehicleArrays;
    }

    public List<List<Vehicle>> filterByCityName(String name) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + FILTER_BY_CITY_NAME);

        List<Vehicle> allVehicles = vehicleRepository.findbycity(name);

        return generatePaginationArray(allVehicles);
    }

    public List<List<Vehicle>> filterByDate(String start, String end) {
        List<Vehicle> allVehicles = vehicleRepository.findByDates(start, end);

        return generatePaginationArray(allVehicles);
    }

    public List<List<Vehicle>> filterByCityDate(String city, String start, String end) {
        List<Vehicle> allVehicles = vehicleRepository.findByCityDate(city, start, end);

        return generatePaginationArray(allVehicles);
    }

    public Vehicle findByPlate(String plate) throws NotInDataBaseException {

        Vehicle vehicle = null;

        try {
            vehicle = vehicleRepository.findByPlate(plate);
        } catch (Exception e) {
            throw new NotInDataBaseException(String.format(VEHICLE_DOES_NOT_EXIST_BY_PLATE, plate));
        }

        return vehicle;

    }

    public Vehicle dtoToEntity(VehicleDto vehicleDto) throws NotInDataBaseException, MissingValuesException, WronglyPopulatedListsException {
        Vehicle vehicleEntity = new Vehicle();

        vehicleEntity.setIdVehicle(vehicleDto.getIdVehicle());
        vehicleEntity.setVehicleType(vehicleTypeService.findVehicleTypeById(vehicleDto.getVehicleType().getIdVehicleType()));

        // Manage Characteristics separately
        manageCharacteristics(vehicleDto.getCharacteristicsList(), vehicleEntity);
        manageUsagePolicies(vehicleDto.getUsagePoliciesList(), vehicleEntity);


        vehicleEntity.setPricePerDay(vehicleDto.getPricePerDay());
        vehicleEntity.setDetails(vehicleDto.getDetails());
        vehicleEntity.setModel(vehicleDto.getModel());
        vehicleEntity.setCity(cityService.findCityById(vehicleDto.getCity().getIdCity()));
        vehicleEntity.setUsagePoliciesList(
                usagePolicyService.findAllById(
                        vehicleDto.getUsagePoliciesList().stream()
                                .map(UsagePolicy::getIdUsagePolicy)
                                .collect(Collectors.toList())
                )
        );
        vehicleEntity.setVehiclePlate(vehicleDto.getVehiclePlate().toUpperCase());

        return vehicleEntity;
    }

    private void manageCharacteristics(List<Characteristic> characteristicsList, Vehicle vehicleEntity) throws NotInDataBaseException {
        try {
            // Try to fetch the characteristics if they exist
            List<Characteristic> characteristics = characteristicService.findAllById(characteristicsList.stream()
                    .map(Characteristic::getIdCharacteristic)
                    .collect(Collectors.toList()));
            vehicleEntity.setCharacteristicsList(characteristics);
        } catch (NotInDataBaseException e) {
            // If some characteristics are missing, attempt to add them
            characteristicsList.forEach(characteristic -> {
                try {
                    characteristicService.addCharacteristic(characteristic);
                } catch (Exception ex) {
                    LOGGER.error("Failed to add characteristic: " + characteristic, ex);
                    // Optionally, handle specific errors here
                }
            });
        }
    }

    private void manageUsagePolicies(List<UsagePolicy> usagePolicyList, Vehicle vehicleEntity) throws NotInDataBaseException {

        LOGGER.info(STARTING_PROCESS + "MANAGE USAGE POLICIES");

        try {
            // Try to fetch the characteristics if they exist
            List<UsagePolicy> usagePolicies = usagePolicyService.findAllById(usagePolicyList.stream()
                    .map(UsagePolicy::getIdUsagePolicy)
                    .collect(Collectors.toList()));
            vehicleEntity.setUsagePoliciesList(usagePolicies);
        } catch (NotInDataBaseException e) {
            // If some characteristics are missing, attempt to add them
            usagePolicyList.forEach(usagePolicy -> {
                try {
                    usagePolicyService.addUsagePolicy(usagePolicy);
                } catch (Exception ex) {
                    LOGGER.error("Failed to add usagePolicy: " + usagePolicy, ex);
                    // Optionally, handle specific errors here
                }
            });
        }
    }

    public VehicleDto entityToDto(Vehicle vehicleEntity) throws NotInDataBaseException, MissingValuesException, WronglyPopulatedListsException {

        VehicleDto vehicleDto = new VehicleDto();

        vehicleDto.setIdVehicle(vehicleEntity.getIdVehicle());
        vehicleDto.setVehicleType(vehicleTypeService.findVehicleTypeById(vehicleEntity.getVehicleType().getIdVehicleType()));
        vehicleDto.setCharacteristicsList(vehicleEntity.getCharacteristicsList());
        vehicleDto.setPricePerDay(vehicleEntity.getPricePerDay());
        vehicleDto.setDetails(vehicleEntity.getDetails());
        vehicleDto.setModel(vehicleEntity.getModel());
        vehicleDto.setCity(cityService.findCityById(vehicleEntity.getCity().getIdCity()));
        vehicleDto.setUsagePoliciesList(vehicleEntity.getUsagePoliciesList());
        vehicleDto.setVehiclePlate(vehicleEntity.getVehiclePlate().toUpperCase());

        return vehicleDto;
    }

    @Transactional
    public void checkAndLinkUsagePolicies(Vehicle vehicleEntity)
            throws MissingValuesException, NotInDataBaseException {
        LOGGER.info(STARTING_PROCESS + "Checking and linking usage policies.");

        // Step 1: Check if policies list is populated
        checkIfPoliciesListIsPopulated(vehicleEntity);

        // Step 2: Link or add policies based on ID presence
        for (UsagePolicy usagePolicy : vehicleEntity.getUsagePoliciesList()) {
            if (usagePolicy.getIdUsagePolicy() != null) {
                usagePolicyService.existsById(usagePolicy.getIdUsagePolicy(), "checking usage policies");
                linkExistingUsagePolicy(vehicleEntity, usagePolicy);
            } else {
                addAndLinkNewUsagePolicy(vehicleEntity, usagePolicy);
            }
        }

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + "Usage policies have been validated and linked.");
    }

    private void checkIfPoliciesListIsPopulated(Vehicle vehicleEntity) throws MissingValuesException {
        if (vehicleEntity.getUsagePoliciesList() == null || vehicleEntity.getUsagePoliciesList().isEmpty()) {
            LOGGER.error("Usage policies list is empty or missing.");
            throw new MissingValuesException("Usage policies list is empty or missing.");
        }
    }

    private void linkExistingUsagePolicy(Vehicle vehicleEntity, UsagePolicy usagePolicy) throws NotInDataBaseException {
        // Check if the UsagePolicy exists in the database by its ID
        UsagePolicy existingPolicy = usagePolicyService.findUsagePolicyById(usagePolicy.getIdUsagePolicy());
            // Link the existing policy to the vehicle
            vehicleEntity.getUsagePoliciesList().add(existingPolicy);
    }

    private void addAndLinkNewUsagePolicy(Vehicle vehicleEntity, UsagePolicy usagePolicy) throws MissingValuesException {
        // Call the service to add a new UsagePolicy to the database
        UsagePolicy savedPolicy = usagePolicyService.addUsagePolicy(usagePolicyService.entityToDto(usagePolicy));
        // Link the newly saved policy to the vehicle
        vehicleEntity.getUsagePoliciesList().add(savedPolicy);
    }


    public void checkListsLength(Vehicle vehicle) throws WronglyPopulatedListsException {

        List<String> attributesWithWrongLength = new ArrayList<>();

        if (vehicle.getCharacteristicsList().size() <= 1) {
            attributesWithWrongLength.add(CHARACTERISTICS);
        }

        if (!attributesWithWrongLength.isEmpty()) {
            throw new WronglyPopulatedListsException(VEHICLES_SHOULD_HAVE + attributesWithWrongLength);
        }
    }

    @Transactional
    public void checkIfValidPoliciesList(Vehicle vehicleEntity)
            throws MissingValuesException, InvalidUsagePolicyTypeException, NotInDataBaseException {
        LOGGER.info(STARTING_PROCESS + "Checking all the usage policies are valid.");

        // Check if policies list is populated and validate usage policy types.
        if (vehicleEntity.getUsagePoliciesList() != null && !vehicleEntity.getUsagePoliciesList().isEmpty()) {
            // Validate usage policies.
            vehicleEntity.getUsagePoliciesList().forEach(
                    usagePolicy -> {
                        if (usagePolicy.getUsagePolicyType() == null) {
                            LOGGER.error("UsagePolicyType is null for usage policy ID: " + usagePolicy.getIdUsagePolicy());
                            throw new InvalidUsagePolicyTypeException("UsagePolicyType cannot be null.");
                        }
                        try {
                            UsagePolicyType.valueOf(usagePolicy.getUsagePolicyType().name());
                        } catch (IllegalArgumentException e) {
                            LOGGER.error("Invalid UsagePolicyType: " + usagePolicy.getUsagePolicyType().name());
                            throw new InvalidUsagePolicyTypeException("Invalid UsagePolicyType: " + usagePolicy.getUsagePolicyType().name());
                        }
                    }
            );
        } else {
            LOGGER.error("Usage policies list is empty or missing.");
            throw new MissingValuesException("Usage policies list is empty or missing.");
        }

        LOGGER.info(PROCESS_FINISHED_SUCCESSFULLY + "Checking all the usage policies are valid.");
    }

    public void checkIfMissingUsagePoliciesTypes(Vehicle vehicle) throws WronglyPopulatedListsException {

        boolean hasPoliticaDeCancelacion = false;
        boolean hasSaludYSeguridad = false;
        boolean hasNormasDeLaCasa = false;

        for (UsagePolicy usagePolicy :
                vehicle.getUsagePoliciesList()) {
            if (usagePolicy.getUsagePolicyType() == UsagePolicyType.POLITICA_DE_CANCELACION)
                hasPoliticaDeCancelacion = true;
            if (usagePolicy.getUsagePolicyType() == UsagePolicyType.SALUD_Y_SEGURIDAD)
                hasSaludYSeguridad = true;
            if (usagePolicy.getUsagePolicyType() == UsagePolicyType.NORMAS_DE_LA_CASA)
                hasNormasDeLaCasa = true;
        }

        if (!(hasNormasDeLaCasa && hasPoliticaDeCancelacion && hasSaludYSeguridad)) {
            throw new WronglyPopulatedListsException(USAGE_POLICIES +
                    POLITICA_DE_CANCELACION + hasPoliticaDeCancelacion +
                    SALUD_Y_SEGURIDAD + hasSaludYSeguridad +
                    NORMAS_DE_LA_CASA + hasNormasDeLaCasa);
        }

    }

    public void checkIfMissingValues(Vehicle vehicle, String process) throws MissingValuesException {

        LOGGER.info(STARTING_PROCESS + "Checking for missing values.");

        if ((vehicle.getCharacteristicsList() == null || vehicle.getCharacteristicsList().isEmpty())
                || (vehicle.getVehicleType() == null)
                || (vehicle.getPricePerDay() == null || vehicle.getPricePerDay().isNaN())
                || (vehicle.getDetails() == null || vehicle.getDetails().isBlank())
                || (vehicle.getModel() == null || vehicle.getModel().isBlank())
                || (vehicle.getCity() == null)
                || (vehicle.getVehiclePlate() == null || vehicle.getVehiclePlate().isBlank())) {
            throw new MissingValuesException(process + FAILED_BECAUSE + MISSING_VALUES);
        }
    }

    public void existsById(Long vehicleId, String desiredAction) throws NotInDataBaseException {

        if (!vehicleRepository.existsById(vehicleId)) {
            LOGGER.error(String.format(desiredAction + FAILED_BECAUSE + VEHICLE_DOES_NOT_EXIST_BY_ID, vehicleId));
            throw new NotInDataBaseException(String.format(desiredAction + FAILED_BECAUSE + VEHICLE_DOES_NOT_EXIST_BY_ID, vehicleId));
        }
    }

    public void validatePlate(VehicleDto vehicleDto) throws NotInDataBaseException, AlreadyExistsInDataBaseException, InvalidValuesException {

        if (!Pattern.matches("^(?=.*[a-zA-Z0-9])[a-zA-Z0-9]{6,11}$", vehicleDto.getVehiclePlate())) {
            throw new InvalidValuesException(WRONG_PLATE_LENGTH);
        }

        if (findByPlate(vehicleDto.getVehiclePlate()) != null) {
            throw new AlreadyExistsInDataBaseException(String.format(VEHICLE_ALREADY_EXIST_BY_PLATE, vehicleDto.getVehiclePlate()));
        }
    }

    private Vehicle copyAllNotNullValues(Vehicle vehicleWithUpdates, Vehicle vehicleToUpdate) {

        if(vehicleWithUpdates.getCharacteristicsList() != null && !vehicleWithUpdates.getCharacteristicsList().isEmpty())
            vehicleToUpdate.setCharacteristicsList(vehicleWithUpdates.getCharacteristicsList());
        if(vehicleWithUpdates.getVehicleType() != null)
            vehicleToUpdate.setVehicleType(vehicleWithUpdates.getVehicleType());
        if(vehicleWithUpdates.getPricePerDay() != null && !vehicleWithUpdates.getPricePerDay().isNaN())
            vehicleToUpdate.setPricePerDay(vehicleWithUpdates.getPricePerDay());
        if(vehicleWithUpdates.getDetails() != null && !vehicleWithUpdates.getDetails().isBlank())
            vehicleToUpdate.setDetails(vehicleWithUpdates.getDetails());
        if(vehicleWithUpdates.getModel() != null && !vehicleWithUpdates.getModel().isBlank())
            vehicleToUpdate.setModel(vehicleWithUpdates.getModel());
        if(vehicleWithUpdates.getVehiclePlate() != null && !vehicleWithUpdates.getVehiclePlate().isBlank())
            vehicleToUpdate.setVehiclePlate(vehicleWithUpdates.getVehiclePlate());

        return vehicleToUpdate;

    }

}
