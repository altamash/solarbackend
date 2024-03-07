package com.solar.api.tenant.model.solarAmps;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestADemoDTO implements Serializable {
    @Length(max = 64, message = "The field must be less than {max} characters")
    private String firstName;
    @Length(max = 64, message = "The field must be less than {max} characters")
    private String lastName;
    @Length(max = 64, message = "The field must be less than {max} characters")
    private String companyName;
    private String email;
    private List<String> servicesYourCompanyProvider;
    private String describeYourBusiness;
    private String sizeOfCompOperation;
    private Date preferredDate;
    @Length(max = 500, message = "The field must be less than {max} characters")
    private String about;
    private String captcha;
}
