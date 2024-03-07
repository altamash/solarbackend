package com.solar.api.tenant.mapper.externalFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalFileDTO {

    private Long importTypeId;
    private String name;
    private String importType;
    private String sourceFormat;
    private String wordSeparator;
    private String lineSeparator;
    private String targetTable;
    private String eofIdentifier;
    private String header;
    private String associatedParser;
}
