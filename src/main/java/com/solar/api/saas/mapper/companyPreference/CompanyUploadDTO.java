package com.solar.api.saas.mapper.companyPreference;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUploadDTO implements Serializable {

    private String freequentlyAskedQuestionsUrl;
    private String logoUrl;
    private List<String> bannerURLs;
}
