package com.solar.api.tenant.mapper.stripe;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class SubmitInterestDTO {

    @Length(max = 64, message = "The field must be less than {max} characters")
    @NotEmpty(message = "Company Name cannot be empty")
    private String companyName;
    @NotNull(message = "Company Type cannot not be null")
    private String companyType;
    @NotNull(message = "Phone Number cannot not be null")
    @Pattern(regexp="(^[+][0-9]{1,3}[0-9]{8,15}$)", message = "Invalid phone number")
    private String phoneNumber;
}
