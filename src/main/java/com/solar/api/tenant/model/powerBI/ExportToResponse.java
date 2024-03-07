package com.solar.api.tenant.model.powerBI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportToResponse {
    /*{
        "@odata.context": "http://wabi-us-north-central-g-primary-redirect.analysis.windows.net/v1
        .0/myorg/groups/77f174e7-788e-4fe9-9042-cf5d7c1a1ccc/$metadata#exports/$entity",
        "id":
        "Mi9CbG9iSWRWMi1jNjNkYzM5OS04OGZiLTQxYjItYmFlYy03YjJkZjJiNWJmZGUwZGJPTzhkRmtjOXZRODdPaTBCMlNDaHJ0aVZmQng3cmRMYzkwd2FGeGY4PS4=",
        "createdDateTime": "2021-01-31T17:23:21.6963949Z",
        "lastActionDateTime": "2021-01-31T17:23:21.6963949Z",
        "reportId": "ba899cd0-7d08-4c48-b4c8-1e5b1da859ae",
        "status": "NotStarted",
        "percentComplete": 0,
        "expirationTime": "0001-01-01T00:00:00Z"
    }*/

    @JsonProperty("@odata.context")
    private String odataContext;
    @JsonProperty("id")
    private String id;
    @JsonProperty("createdDateTime")
    private String createdDateTime;
    @JsonProperty("lastActionDateTime")
    private String lastActionDateTime;
    @JsonProperty("reportId")
    private String reportId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("percentComplete")
    private Integer percentComplete;
    @JsonProperty("expirationTime")
    private String expirationTime;
}
