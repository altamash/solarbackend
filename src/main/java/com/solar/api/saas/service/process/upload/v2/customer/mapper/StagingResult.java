package com.solar.api.saas.service.process.upload.v2.customer.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.CustomerStageTable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StagingResult {

    private String uploadId;
    private List<CustomerStageTable> stagedCustomers;
    private String fileStorageUri;

    @Override
    public String toString() {
        return "StagingResult{" +
                "uploadId='" + uploadId + '\'' +
                ", stagedCustomers=" + stagedCustomers +
                ", fileStorageUri='" + fileStorageUri + '\'' +
                '}';
    }
}
