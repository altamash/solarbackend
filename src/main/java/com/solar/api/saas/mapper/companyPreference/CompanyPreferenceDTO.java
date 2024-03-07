package com.solar.api.saas.mapper.companyPreference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.document.DocuLibraryDTO;
import lombok.*;

import javax.persistence.Column;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyPreferenceDTO {
    private Long id;
    private String companyCode;
    private Long companyKey;
    private Long locId;
    private String companyName;
    private String logo;
    private Integer emergencySupportNumber;
    private String countryCode;
    private String country;
    private String helpline247;
    private String defaultCurrency;
    private String pictureRollingDelay;
    private String facebookURL;
    private String twitterURL;
    private String youtubeURL;
    private String linkedInURL;
    private String faqURL;
    private List<BannerDTO> banner;
    private String websiteURL;
    private String companyPolicy;
    private String emailAddress;
    private Long orgId;
    private List<DocuLibraryDTO> landingPageImages;
    private List<DocuLibraryDTO> mobileLandingPageImages;
    private String cpConfigure;
    private String adminWelcomeWidgetText;
    private String customerWelcomeWidgetText;
    private String companyTerms;
    private String landingDescription;
    private String landingText;


    public CompanyPreferenceDTO(Long id,String websiteURL, String youtubeURL, String twitterURL,String linkedInURL,String facebookURL,String companyTerms, String companyPolicy, String landingText,String landingDescription) {
        this.id = id;
        this.facebookURL = facebookURL;
        this.twitterURL = twitterURL;
        this.youtubeURL = youtubeURL;
        this.linkedInURL =linkedInURL;
        this.websiteURL = websiteURL;
        this.companyTerms=companyTerms;
        this.companyPolicy=companyPolicy;
        this.landingText=landingText;
        this.landingDescription=landingDescription;

    }
    public CompanyPreferenceDTO(Long id, String youtubeURL, String twitterURL,String linkedInURL,String facebookURL,String companyTerms, String companyPolicy) {
        this.id = id;
        this.facebookURL = facebookURL;
        this.twitterURL = twitterURL;
        this.youtubeURL = youtubeURL;
        this.linkedInURL =linkedInURL;
        this.companyTerms=companyTerms;
        this.companyPolicy=companyPolicy;

    }


}
