package com.solar.api.saas.service.integration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteJson {
    public Long siteId;
    @JsonProperty("locations")
    public ArrayList<LocationJson> locations;
}
