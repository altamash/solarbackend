package com.solar.api.tenant.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = -5327628407240350793L;
    @NotNull(message = "User Name  Cannot Not Be Null")
    @NotBlank(message = "User Name cannot not be Blank")
    private String userName;
    @NotNull(message = "User Password  Cannot Not Be Null")
    @NotBlank(message = "User Password  Cannot Not Be Blank")
    private String password;
    private Boolean isMobileLogin;
}
