package com.solar.api.tenant.model.stage.monitoring;
import lombok.*;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtDataStageDefinitionDTO {
    private Long id;
    private String refType; // (Variant)
    private String refId; // (variant ID)
    private String subsId; // “334kj34lk534hlh“
    private String authType; // standard
    private String authU;
    private String authP;
    private String brand;
    private String monPlatform;
    private String mpJson;
    private String subsStatus;
    private String inverterStatus;
    //    private String lastUpdateDate
    private String groupId; // (not used but can be used incase of partial refresh)
    private Map<String, List<String>> variantMap;
    private String subscriptionName;
    private String systemSize;
    private Long custAdd;
    private  Long acctId;
    private ExtDataStageDefinition extDataStageDefinition;
    private String extJson;
    private String gardenImageUri;


    public ExtDataStageDefinitionDTO(ExtDataStageDefinition ex, Long acctId) {
        this.extDataStageDefinition = ex;
        this.acctId = acctId;
    }
    public ExtDataStageDefinitionDTO(ExtDataStageDefinition ex, String gardenImageUri) {
        this.extDataStageDefinition = ex;
        this.gardenImageUri = gardenImageUri;
    }

    public ExtDataStageDefinitionDTO(String refId,String monPlatform,String refType,String subsStatus,
                                     Long id,String brand,String extJson, String mpJson,String uri){
        this.refId = refId;
        this.monPlatform = monPlatform;
        this.refType = refType;
        this.subsStatus = subsStatus;
        this.id = id;
        this.brand = brand;
        this.extJson = extJson;
        this.mpJson = mpJson;
        this.gardenImageUri = uri;
    }
}