package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.document.DocuLibraryDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationReferenceDTO {
    private Long id;
    private String uri;
    private Long referenceId;
    private String referenceType;
    private DocuLibraryDTO docuLibrary;
}
