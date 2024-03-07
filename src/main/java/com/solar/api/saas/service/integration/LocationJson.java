package com.solar.api.saas.service.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationJson {
        public Long physical_loc_Id;
        public boolean enabled;
}

