package com.solar.api.saas.service.process.upload.health;

import com.google.common.base.Joiner;
import com.solar.api.helper.Utility;
import com.solar.api.helper.ValidationUtils;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import com.solar.api.tenant.repository.project.ProjectInventoryRepository;
import com.solar.api.tenant.repository.project.ProjectInventorySerialRepository;
import com.solar.api.tenant.service.extended.register.RegisterDetailService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ProjectUploadHealthCheckImpl extends AbstractUploadHealthCheck implements ProjectUploadHealthCheck {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    @Lazy
    private RegisterDetailService registerDetailService;
    @Autowired
    @Lazy
    private AssetSerialNumberRepository assetSerialNumberRepository;
    @Autowired
    @Lazy
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    private ProjectInventoryRepository projectInventoryRepository;
    @Autowired
    private ProjectInventorySerialRepository projectInventorySerialRepository;


    @Override
    public HealthCheckResult validate(List<Map<?, ?>> mappings, Long registerId, String action, Long assetId, Long projectId) {
        List<HealthCheck> checks = new ArrayList<>();
        List<String> columnFieldsNotEmpty = new LinkedList<>();
        List<RegisterDetail> registerDetails = null;

        try {

            if (assetId != null && projectId!=null) {
                //project inventory
                AssetHead assetHead = assetHeadRepository.findById(assetId).get();
                registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(assetHead.getRegisterId());
                columnFieldsNotEmpty.add("Pallet Number");

            } else if (registerId != null) {
                registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(registerId);
            }

            //unique field should be in measure definition
            AtomicReference<String> uniqueMeasure = new AtomicReference<>(null);
            List<MeasureDefinitionTenantDTO> measures = null;
            //if (registerDetails !=null && registerDetails.size()!=0) {

            registerDetails.forEach( unique -> {
                if(unique.getMeasureUnique()) {
                    uniqueMeasure.set(measureDefinitionOverrideService.findById(unique.getMeasureCodeId()).getMeasure());
                    columnFieldsNotEmpty.add(uniqueMeasure.toString());
                }
            });

            measures = measureDefinitionOverrideService.findByIds(registerDetails.stream()
                            .filter(m -> m.getMeasureCodeId() != null).map(m -> m.getMeasureCodeId()).collect(Collectors.toList()));

            //if (mappings.get(0).get(uniqueMeasure) != null) {
                checkMandatoryFieldsNotEmpty(checks, mappings, columnFieldsNotEmpty.toArray(new String[0]));
                if (assetId != null) {
                    checkSerialNumberNotLinkedWithSameAsset(checks, null, mappings, uniqueMeasure.toString(), action, assetId);
                }
                checkFormatOfFields(checks, mappings, measures);
                checkMultipleOccurrences(checks, mappings, uniqueMeasure.toString());

                if (registerId != null && !action.equals(null)) {
                    insertWithExistingSerialNumberNotAllowed(checks, null, mappings, uniqueMeasure.toString(), action);//later on supplier match will apply with serialNumber
                }

/*            } else {
                return HealthCheckResult.builder()
                        .totalRows(0).correctRowIds("").healthChecks(null).build();
                        //.errorMessage("Invalid headers").build();
            }*/

            checks.sort(Comparator.comparing(HealthCheck::getLine));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            LOGGER.error(ex.getMessage());
        }

        return healthCheckResult(mappings,checks);
    }

/*    @Override
    public HealthCheckResult validateProjectInventory(List<Map<?, ?>> mappings, Long assetId, Long projectInventoryId) {
        return null;
    }*/

    int checkMandatoryFieldsNotEmpty(List<HealthCheck> checks, List<Map<?, ?>> mappings, String... fields) {
        List<String> emptyFieldsList = new ArrayList<>();
        for (int i = 0; i < mappings.size(); i++) {
            List<String> emptyFields = mappings.get(i).entrySet().stream()
                    .filter(m -> ((String) m.getValue()).isEmpty() && Arrays.asList(fields).contains(((String) m.getKey())))
                    .map(m -> (String) m.getKey()).collect(Collectors.toList());
            if (!emptyFields.isEmpty()) {
                addParseError(checks, i, mappings.get(i),
                        Joiner.on(", ").join(emptyFields) + (emptyFields.size() == 1 ? " is" : " are") + " mandatory");
                emptyFieldsList.addAll(emptyFields);
            }
        }
        return emptyFieldsList.size();
    }

    //TODO:measure format check
    private int checkFormatOfFields(List<HealthCheck> checks, List<Map<?, ?>> mappings,
                                    List<MeasureDefinitionTenantDTO> mandatoryMeasures) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            Iterator<? extends Map.Entry<?, ?>> iterator = mapping.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> map = iterator.next();
                Optional<MeasureDefinitionTenantDTO> measureOptional =
                        mandatoryMeasures.stream().filter(m -> m.getMeasure().equalsIgnoreCase(map.getKey().toString())).findFirst();
                if (measureOptional.isPresent() && measureOptional.get().getFormat() != null) {
                    String fieldValue = (String) map.getValue();
                    switch (measureOptional.get().getFormat()) {
                        case "NUM":
                        case "NUMBER":
                            if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isNumeric(fieldValue)) {
                                addParseError(checks, i, mappings.get(i), map.getKey() + " must be numeric");
                                count++;
                            }
                            break;
                        /*case "TEXT":
                            if (fieldValue != null && !fieldValue.isEmpty() && ValidationUtils.isNumeric(fieldValue)) {
                                addParseError(checks, i, mappings.get(i), map.getKey() + " must be text");
                                count++;
                            }
                            break;*/
                        case "DATE":
                            if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidDate(fieldValue, Utility.SYSTEM_DATE_FORMAT)) {
                                addParseError(checks, i, mappings.get(i),
                                map.getKey() + " must be a date in format " + Utility.SYSTEM_DATE_FORMAT);
                                count++;
                            }
                    }
                }
            }
        }
        return count;
    }

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .issue(issue)
                .build());
    }

    @Override
    int checkRequiredFieldsInHeader(List<HealthCheck> checks, Set<?> fileHeader, String... fields) {
        return 0;
    }

    //TODO:serial number is unique in supplier
    private int insertWithExistingSerialNumberNotAllowed(List<HealthCheck> checks, Long supplierId,
                                                         List<Map<?, ?>> mappings,
                                                         String uniqueMeasure, String action) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String serialNumber = (String) mapping.get(uniqueMeasure);
            //query
            AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findBySerialNumber(serialNumber);
            if (action.equals(EUploadAction.INSERT.getAction()) && assetSerialNumber != null ) {
                    //if (action.equals(EUploadAction.INSERT.getAction()) || (action.equals(EUploadAction.UPDATE.getAction())) {
                        addParseError(checks, i, mappings.get(i),
                                "Serial Number '" + serialNumber + "' " + "already exists ");
                count++;
            } else if (action.equals(EUploadAction.UPDATE.getAction()) && assetSerialNumber == null ) {
                addParseError(checks, i, mappings.get(i),
                        "Serial Number '" + serialNumber + "' " + "doesn't exists ");
                count++;
            }
        }
        return count;
    }

    //TODO:serial number for project inventory
    private int checkWithExistingSerialNumber(List<HealthCheck> checks, Long supplierId,
                                                         List<Map<?, ?>> mappings,
                                                         String uniqueMeasure, Long assetId, Long projectId) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String serialNumber = (String) mapping.get(uniqueMeasure);
            //String palletNo = (String) mapping.get("Pallet No.");

            //query
            AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findBySerialNumber(serialNumber);
            //AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findBySerialNumberAndPalletNo(serialNumber,palletNo);
            if (assetSerialNumber != null ) {
                ProjectInventorySerial projectInventorySerial = projectInventorySerialRepository.findByAssetSerialNumberId(assetSerialNumber.getId());
                if (projectInventorySerial != null) {
                    ProjectInventory projectInventory = projectInventoryRepository.findByAssetIdAndProjectId(assetId,projectId);
                    if (!projectInventory.getId().equals(projectInventorySerial.getProjectInventoryId())) {
                        addParseError(checks, i, mappings.get(i),
                                "Serial Number '" + serialNumber + "' " + "is already linked with current project inventory");
                        //count++;
                    }
                }
            }

        }
        return count;
    }

    int checkMultipleOccurrences(List<HealthCheck> checks, List<Map<?, ?>> mappings, String field) {
        int count = 0;
        List<?> fieldValuesInFile = mappings.stream().map(m -> m.get(field)).collect(Collectors.toList());

        //if (mappings.stream().filter(m -> m.get(field) != null).findAny().isPresent()) {
           // if (!fieldValuesInFile.isEmpty()) {
                for (int i = 0; i < mappings.size(); i++) {
                    String fieldValue = (String) mappings.get(i).get(field);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        int frequency = Collections.frequency(fieldValuesInFile, mappings.get(i).get(field));
                        if (frequency > 1) {
                            addParseError(checks, i, mappings.get(i),
                                    "Multiple occurrences of " + field + " " + fieldValue + " found in file");
                            count++;
                        }
                    }
                }
            //}
        //}
        return count;
    }

    //TODO:serial number is not linked with same asset
    private int checkSerialNumberNotLinkedWithSameAsset(List<HealthCheck> checks, Long supplierId,
                                                         List<Map<?, ?>> mappings,
                                                         String uniqueMeasure, String action, Long assetId) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String serialNumber = (String) mapping.get(uniqueMeasure);
            //query
            AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findBySerialNumber(serialNumber);
            if (assetSerialNumber!=null) {
                if (assetSerialNumber.getAssetId() != assetId) {
                    addParseError(checks, i, mappings.get(i),
                            "Serial Number '" + serialNumber + "' " + " is linked with asset id : " + assetSerialNumber.getAssetId());
                    count++;
                }

            }
        }
        return count;
    }
}
