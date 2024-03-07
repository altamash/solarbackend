package com.solar.api.tenant.mapper.projectListing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagerDTO implements Serializable {

    private Long acctId;
    private Long compKey;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String gender;
    private Date dataOfBirth;
    private String status;
    private String userType;
    private Set<String> roles;
    private String category;
    private byte[] photo;
    private String photoBase64;
    private String emailAddress;
    private String profileUrl;
    private Integer privLevel;
    private String roleName;
    private Long roleId;

}
