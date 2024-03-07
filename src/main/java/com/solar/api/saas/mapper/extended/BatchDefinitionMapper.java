package com.solar.api.saas.mapper.extended;

import com.solar.api.saas.model.extended.BatchDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class BatchDefinitionMapper {

    public static BatchDefinition toBatchDefinition(BatchDefinitionDTO batchDefinitionDTO) {
        if (batchDefinitionDTO == null) {
            return null;
        }
        return BatchDefinition.builder()
                .id(batchDefinitionDTO.getId())
                .jobName(batchDefinitionDTO.getJobName())
                .functionalArea(batchDefinitionDTO.getFunctionalArea())
                .type(batchDefinitionDTO.getType())
                .phase(batchDefinitionDTO.getPhase())
                .preDependency(batchDefinitionDTO.getPreDependency())
                .cronExpression(batchDefinitionDTO.getCronExpression())
                .postDependency(batchDefinitionDTO.getPostDependency())
                .runNotes(batchDefinitionDTO.getRunNotes())
                .frequency(batchDefinitionDTO.getFrequency())
                .parameters(batchDefinitionDTO.getParameters())
                .startTime(batchDefinitionDTO.getStartTime())
                .endTime(batchDefinitionDTO.getEndTime())
                .build();
    }

    public static BatchDefinitionDTO toBatchDefinitionDTO(BatchDefinition batchDefinition) {
        if (batchDefinition == null) {
            return null;
        }
        return BatchDefinitionDTO.builder()
                .id(batchDefinition.getId())
                .jobName(batchDefinition.getJobName())
                .functionalArea(batchDefinition.getFunctionalArea())
                .type(batchDefinition.getType())
                .phase(batchDefinition.getPhase())
                .preDependency(batchDefinition.getPreDependency())
                .cronExpression(batchDefinition.getCronExpression())
                .postDependency(batchDefinition.getPostDependency())
                .runNotes(batchDefinition.getRunNotes())
                .frequency(batchDefinition.getFrequency())
                .parameters(batchDefinition.getParameters())
                .startTime(batchDefinition.getStartTime())
                .endTime(batchDefinition.getEndTime())
                .build();
    }

    public static BatchDefinition toUpdatedBatchDefinition(BatchDefinition batchDefinition,
                                                           BatchDefinition batchDefinitionUpdate) {
        batchDefinition.setJobName(batchDefinition.getJobName() == null ? batchDefinition.getJobName() :
                batchDefinition.getJobName());
        batchDefinition.setFunctionalArea(batchDefinition.getFunctionalArea() == null ?
                batchDefinition.getFunctionalArea() : batchDefinition.getFunctionalArea());
        batchDefinition.setType(batchDefinition.getType() == null ? batchDefinition.getType() :
                batchDefinition.getType());
        batchDefinition.setPhase(batchDefinition.getPhase() == null ? batchDefinition.getPhase() :
                batchDefinition.getPhase());
        batchDefinition.setPreDependency(batchDefinition.getPreDependency() == null ?
                batchDefinition.getPostDependency() : batchDefinition.getPreDependency());
        batchDefinition.setCronExpression(batchDefinition.getCronExpression() == null ?
                batchDefinition.getCronExpression() : batchDefinition.getCronExpression());
        batchDefinition.setPostDependency(batchDefinition.getPostDependency() == null ?
                batchDefinition.getPostDependency() : batchDefinition.getPostDependency());
        batchDefinition.setRunNotes(batchDefinition.getRunNotes() == null ? batchDefinition.getRunNotes() :
                batchDefinition.getRunNotes());
        batchDefinition.setFrequency(batchDefinition.getFrequency() == null ? batchDefinition.getFrequency() :
                batchDefinition.getFrequency());
        batchDefinition.setParameters(batchDefinition.getParameters() == null ? batchDefinition.getParameters() :
                batchDefinition.getParameters());
        batchDefinition.setStartTime(batchDefinition.getStartTime() == null ? batchDefinition.getStartTime() :
                batchDefinition.getStartTime());
        batchDefinition.setEndTime(batchDefinition.getEndTime() == null ? batchDefinition.getEndTime() :
                batchDefinition.getEndTime());
        return batchDefinition;
    }

    public static List<BatchDefinition> toBatchDefinitions(List<BatchDefinitionDTO> batchDefinitionDTOS) {
        return batchDefinitionDTOS.stream().map(a -> toBatchDefinition(a)).collect(Collectors.toList());
    }

    public static List<BatchDefinitionDTO> toBatchDefinitionDTOs(List<BatchDefinition> batchDefinitions) {
        return batchDefinitions.stream().map(a -> toBatchDefinitionDTO(a)).collect(Collectors.toList());
    }
}
