package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityDetailDTO {

    private Long id;
    private EntityDTO entityDTO;
    //private String iconData; //base64
    private String uri;
    private String fileName;

}
