package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceDTO;
import com.solar.api.saas.mapper.companyPreference.CompanyUploadDTO;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.CompanyPreferenceService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.saas.mapper.companyPreference.CompanyPreferenceMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CompanyPreferenceController")
@RequestMapping(value = "/company")
public class CompanyPreferenceController {

    @Autowired
    private CompanyPreferenceService companyPreferenceService;
    @Autowired
    private MasterTenantService masterTenantService;

    @Autowired
    private PhysicalLocationService physicalLocationService;

    @Autowired
    private UserService userService;

    @PostMapping("/preference/add")
    public CompanyPreferenceDTO add(@RequestBody CompanyPreferenceDTO companyPreferenceDTO) {
        return toCompanyPreferenceDTO(companyPreferenceService.addOrUpdate(toCompanyPreference(companyPreferenceDTO)));
    }

    @PutMapping("/preference/edit")
    public CompanyPreferenceDTO update(@RequestBody CompanyPreferenceDTO companyPreferenceDTO) {
        return toCompanyPreferenceDTO(companyPreferenceService.addOrUpdate(toCompanyPreference(companyPreferenceDTO)));
    }

    @GetMapping("/getPreference/{id}")
    public CompanyPreferenceDTO findById(@PathVariable Long id) {
        return toCompanyPreferenceDTO(companyPreferenceService.getCurrentCompanyPreference());
    }

    @GetMapping("/companyKey/{id}")
    public CompanyPreferenceDTO findByCompanyKey(@PathVariable Long id) {
        return toCompanyPreferenceDTO(companyPreferenceService.getCurrentCompanyPreference());
    }

    @GetMapping("/getAllPreference")
    public List<CompanyPreferenceDTO> findAll() {
        return toCompanyPreferenceDTOs(companyPreferenceService.findAll());
    }

    @GetMapping("/compkey/{identifier}")
    public ObjectNode getCompanyKey(@PathVariable String identifier) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        CompanyPreference companyPreference = null;
        MasterTenant company = masterTenantService.findByCompanyCode(identifier);
        User currentUser = userService.getLoggedInUser();
        if (company != null) {

            DBContextHolder.setTenantName(company.getDbName());
            DBContextHolder.setLegacy(company.getLegacyBilling());

            companyPreference = companyPreferenceService.findByCompanyKey(company.getCompanyKey());
            if (companyPreference.getLocId() != null) {
                PhysicalLocation physicalLocation = physicalLocationService.findPhysicalLocationById(companyPreference.getLocId());
                messageJson.put("latitude", physicalLocation.getGeoLat());
                messageJson.put("longitude", physicalLocation.getGeoLong());
            }

            messageJson.put("compKey", company.getCompanyKey());
            messageJson.put("identifier", company.getCompanyCode());
            messageJson.put("companyName", company.getCompanyName());
            messageJson.put("companyLogo", company.getCompanyLogo());
            messageJson.put("defaultCurrency", companyPreference.getDefaultCurrency());
            messageJson.put("websiteURL", companyPreference.getWebsiteURL());
            messageJson.put("companyPolicy", companyPreference.getCompanyPolicy());
            messageJson.put("facebookURL", companyPreference.getFacebookURL());
            messageJson.put("twitterURL", companyPreference.getTwitterURL());
            messageJson.put("youtubeURL", companyPreference.getYoutubeURL());
            messageJson.put("linkedInURL", companyPreference.getLinkedInURL());
            if (currentUser.getUserType().getId() == 2) {
                messageJson.put("adminLandingPageURL", companyPreference.getAdminLanding() != null ?
                        companyPreference.getAdminLanding() : null);
            }
        }
        else {
            messageJson.put("latitude","Company Not Found");
            messageJson.put("longitude", "Company Not Found");
            messageJson.put("compKey", "Company Not Found");
            messageJson.put("identifier", identifier);
            messageJson.put("companyName", "Company Not Found");
            messageJson.put("companyLogo", "Company Not Found");
            messageJson.put("defaultCurrency", "Company Not Found");
            messageJson.put("websiteURL", "Company Not Found");
            messageJson.put("companyPolicy", "Company Not Found");
            messageJson.put("facebookURL", "Company Not Found");
            messageJson.put("twitterURL", "Company Not Found");
            messageJson.put("youtubeURL", "Company Not Found");
            messageJson.put("linkedInURL", "Company Not Found");
            messageJson.put("adminLandingPageURL","Company Not Found");

        }

        return messageJson;
    }

    @DeleteMapping("/deletePreference/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        companyPreferenceService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAllPreference")
    public ResponseEntity deleteAll() {
        companyPreferenceService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * GetFaq
     *
     * @param compKey
     * @return
     */
    @GetMapping("/getCompanyFAQ/{compKey}")
    public CompanyUploadDTO getFaq(@PathVariable Long compKey) {
        return CompanyUploadDTO.builder().freequentlyAskedQuestionsUrl(companyPreferenceService.getCurrentCompanyFaqUrl()).build();
    }

    /**
     * GetLogo
     *
     * @param compKey
     * @return
     */
    @GetMapping("/getCompanyLogo/{compKey}")
    public CompanyUploadDTO getLogo(@PathVariable Long compKey) {
        return CompanyUploadDTO.builder().logoUrl(companyPreferenceService.getCurrentCompanyLogoUrl()).build();
    }

    /**
     * GetBanners
     *
     * @param compKey
     * @return
     */
    @GetMapping("/getCompanyBanners/{compKey}")
    public CompanyUploadDTO getBanners(@PathVariable Long compKey) {
        return CompanyUploadDTO.builder().bannerURLs(companyPreferenceService.getCurrentCompanyBannerUrls()).build();
    }

    @GetMapping("/compkey/v1")
    public ObjectNode getCompanyKeyV1(@RequestBody String identifier) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        MasterTenant company = masterTenantService.findByCompanyCode(identifier);
        DBContextHolder.setTenantName(company.getDbName());
        DBContextHolder.setLegacy(company.getLegacyBilling());
        CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(company.getCompanyKey());
        if (companyPreference.getLocId() != null) {
            PhysicalLocation physicalLocation = physicalLocationService.findPhysicalLocationById(companyPreference.getLocId());
            messageJson.put("latitude", physicalLocation.getGeoLat());
            messageJson.put("longitude", physicalLocation.getGeoLong());
        }
        messageJson.put("compKey", company.getCompanyKey());
        messageJson.put("identifier", company.getCompanyCode());
        messageJson.put("companyName", company.getCompanyName());
        messageJson.put("companyLogo", company.getCompanyLogo());
        messageJson.put("defaultCurrency", companyPreference.getDefaultCurrency());
        messageJson.put("websiteURL", companyPreference.getWebsiteURL());
        messageJson.put("companyPolicy", companyPreference.getCompanyPolicy());
        messageJson.put("facebookURL", companyPreference.getFacebookURL());
        messageJson.put("twitterURL", companyPreference.getTwitterURL());
        messageJson.put("youtubeURL", companyPreference.getYoutubeURL());
        messageJson.put("linkedInURL", companyPreference.getLinkedInURL());
        return messageJson;
    }
}
