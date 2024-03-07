package com.solar.api.saas.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JwtRequestTenant implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;
    private String userName;
    private String passCode;
}
