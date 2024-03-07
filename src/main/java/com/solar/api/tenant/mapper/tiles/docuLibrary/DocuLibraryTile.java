package com.solar.api.tenant.mapper.tiles.docuLibrary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocuLibraryTile {

    private Long id;
    private String status;
    private String templateName;
    private String organizationName;
    private String customerType;
    private String uri;

    public DocuLibraryTile(Long id, String status, String templateName, String organizationName, String customerType, String uri) {
        this.id = id;
        this.status = status;
        this.templateName = templateName;
        this.organizationName = organizationName;
        this.customerType = customerType;
        this.uri = uri;
    }
}
