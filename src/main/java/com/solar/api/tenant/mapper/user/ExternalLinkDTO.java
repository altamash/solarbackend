package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalLinkDTO implements Serializable {

    private String token;
    private Long compKey;


}
