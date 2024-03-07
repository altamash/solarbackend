package com.solar.api.saas.service.integration;

import com.solar.api.exception.SolarApiException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.service.extended.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController("ProjectWrapperController")
@RequestMapping(value = "/projectWrapper")

public class ProjectWrapperController {

    @Autowired
    private ProjectService projectService;
    @Value("${app.mongoBaseUrl}")
    private String mongoBaseUrl;

    @Autowired
    private DataExchange dataExchange;

    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showTemplateSection/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showTemplateSection(@PathVariable("projectId") String projectId,
                                              @RequestHeader("Priv-Level") String privLevel) throws SolarApiException {
//                                              @RequestHeader("Tenant-id") String tenantId)

//        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/project/showTemplateSections/%s";
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/project/showTemplateSections/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(privLevel));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, projectId), null, headers, String.class);
        return staticValues;
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/template/saveOrUpdateTemplate/{reqType}")
    public BaseResponse save(@PathVariable("reqType") String reqType,
                             @RequestParam(name = "template", required = true) String template,
                             @RequestHeader("PrivLevelTenantConfig") String privLevelTenantConfig,
                             @RequestHeader("Priv-LevelUser") String privLevelUser) throws SolarApiException {
        //String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/project/template/saveOrUpdateTemplate/%s";
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/project/template/saveOrUpdateTemplate/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("PrivLevelTenantConfig", Arrays.asList(privLevelTenantConfig));
        headers.put("Priv-LevelUser", Arrays.asList(privLevelUser));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("template", template);
        ResponseEntity<BaseResponse> response = WebUtils.submitRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL, reqType), map, headers, BaseResponse.class);
        return response.getBody();
    }

    /*@GetMapping(value = "/showTemplateSection/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showTemplateSection(@PathVariable("projectId") String projectId,
                                              @RequestHeader("Priv-Level") String privLevel) throws SolarApiException {
//                                              @RequestHeader("Tenant-id") String tenantId)

//        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/project/showTemplateSections/%s";
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/project/showTemplateSections/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(privLevel));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL,projectId), null, headers, String.class);
        return staticValues;
    }*/

    @PreAuthorize("checkAccess()")
    @PostMapping("/updateSectionContent/{projectId}/{reqType}")
    public BaseResponse updateSectionContent(@PathVariable("projectId") String projectId,
                                             @PathVariable("reqType") String reqType,
                                             @RequestParam("sectionJson") String sectionJson,
                                             @RequestHeader("Priv-Level") String privLevel) {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/project/updateSectionContent/%s/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        headers.put("Priv-Level", Arrays.asList(privLevel));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sectionJson", sectionJson);
        ResponseEntity<BaseResponse> response = WebUtils.submitRequest(HttpMethod.POST, String.format(DYNAMIC_MAPPINGS_URL, projectId, reqType), map, headers, BaseResponse.class);
        return response.getBody();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showProductByTmpInd/{tmpInd}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showProductByTmpInd(@PathVariable Boolean tmpInd) {
//        @RequestHeader("Tenant-id") String tenantId
//        String DYNAMIC_MAPPINGS_URL = "https://simongo.azurewebsites.net/productsapi/product/showProductByTmpInd/%s";
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showProductByTmpInd/%s";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL, tmpInd), null, headers, String.class);
        return staticValues;
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showTenantMeasuresByType", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showTenantMeasuresByType(@RequestParam(value = "type", required = false) Long type) {
        String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/measures/showTenantMeasuresByType?type={type}";
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
        ResponseEntity<String> staticValues =
                WebUtils.submitRequest(HttpMethod.GET, DYNAMIC_MAPPINGS_URL, null, headers, String.class, type);
        return staticValues;
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/showProjectListings")
    public Map showProjectListings(@RequestHeader("Comp-Key") Long compKey,
                                   @RequestParam(value = "pageSize", required = false) Integer size,
                                   @RequestParam("pageNumber") Integer pageNumber,
                                   @RequestParam("groupBy") String groupBy,
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(value = "template", required = false) String template,
                                   @RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "owner", required = false) String owner,
                                   @RequestParam(value = "createdAt", required = false) String createdAt,
                                   @RequestParam(value = "searchWords", required = false) String searchWords,
                                   @RequestParam(value = "loggedInUserAcctId", required = false) Long loggedInUserAcctId,
                                   @RequestParam(value = "loggedInUserEntityId", required = false) Long loggedInUserEntityId,
                                   @RequestParam(value = "privLevel", required = true) String privLevel) {
        Map response = new HashMap();
        if (pageNumber == null || groupBy == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            response = projectService.showProjectListings(response, size, pageNumber, groupBy, name, status, template, type, owner, createdAt, searchWords, loggedInUserAcctId, privLevel, loggedInUserEntityId);
        }
        return response;
    }

    @Deprecated
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllProjectListingsWithFilters")
    public Map getAllProjectListingsWithFilters(@RequestHeader("Tenant-id") String tenantId,
                                                @RequestParam(value = "pageSize", required = false) Integer size,
                                                @RequestParam("pageNumber") Integer pageNumber,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "template", required = false) String template,
                                                @RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "owner", required = false) String owner,
                                                @RequestParam(value = "createdAt", required = false) String createdAt) {
        Map response = new HashMap();
        if (pageNumber == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            response = projectService.getAllProjectListingsWithFilters(response, size, pageNumber, status, template, type, owner, createdAt);
        }
        return response;
    }


    @GetMapping(value = "/showAcquisitionTemplate/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showAcquisitionTemplate(@PathVariable("projectId") String projectId,
                                                  @RequestHeader("Priv-Level") String privLevel,
                                                  @RequestHeader("Comp-Key") Long compKey
    ) throws SolarApiException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        return dataExchange.showAcquisitionTemplate(projectId, privLevel);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showSectionContent/{projectId}/{sectionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity showSectionContent(@PathVariable("projectId") String projectId,
                                             @PathVariable("sectionId") String sectionId,
                                             @RequestHeader("Priv-Level") String privLevel) throws SolarApiException {

        return dataExchange.showSectionContent(projectId, sectionId, privLevel);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showHierarchySectionDetail/{projectId}/{sectionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Object showHierarchySectionDetail(@PathVariable("projectId") String projectId,
                                             @PathVariable("sectionId") String sectionId,
                                             @RequestParam(value = "loggedInUserAcctId", required = false) Long loggedInUserAcctId,
                                             @RequestParam(value = "loggedInUserEntityId", required = false) Long loggedInUserEntityId,
                                             @RequestHeader("Tenant-id") String tenantId,
                                             @RequestHeader("Priv-Level") String privLevel) throws SolarApiException {

        ResponseEntity response = projectService.showHierarchySectionDetail(projectId, sectionId, privLevel, loggedInUserAcctId, loggedInUserEntityId);
        if (response != null) {
            return response.getBody();
        }
        return null;
    }

    @GetMapping("/getFilterDropDown/v1")
    public BaseResponse getOrgOfficeFilters(@RequestHeader("Comp-Key") Long compKey) {
        return projectService.showProjectListingFilterDropDown();
    }
}
