package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataBlock {

    @JsonProperty("ref_block_id")
    private String refBlockId;
    private String title;
    @JsonProperty("block_collection_name")
    private String blockCollectionName;
}
