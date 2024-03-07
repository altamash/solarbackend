package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.project.*;
import com.solar.api.tenant.mapper.extended.resources.ResourceAttendanceLogDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.project.ProjectDetail;
import com.solar.api.tenant.model.extended.project.ProjectResourceEngagement;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.project.ProjectDetailRepository;
import com.solar.api.tenant.repository.project.ProjectHeadRepository;
import com.solar.api.tenant.service.extended.assetHead.AssetBlockDetailService;
import com.solar.api.tenant.service.extended.project.*;
import com.solar.api.tenant.service.extended.register.RegisterDetailService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.*;
import static com.solar.api.tenant.mapper.extended.resources.ResourceAttendanceLogMapper.toResourceAttendanceLogDTOs;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ProjectController")
@RequestMapping(value = "/project")
public class ProjectController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectHeadRepository projectHeadRepository;
    @Autowired
    private ProjectDetailRepository projectDetailRepository;
    @Autowired
    private ProjectPartnerService projectPartnerService;
    @Autowired
    private ProjectResourceEngagementService projectResourceEngagementService;
    @Autowired
    private ProjectInventoryService projectInventoryService;
    @Autowired
    private EngagementRateGroupsService engagementRateGroupsService;
    @Autowired
    private ProjectSiteService projectSiteService;
    @Autowired
    private RegisterDetailService registerDetailService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private ResourceAttendanceLogService resourceAttendanceLogService;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;
    @Autowired
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    private AssetBlockDetailService assetBlockDetailService;

    // ProjectHead //////////////////////////////////////
    @PostMapping("/head")
    public ProjectHeadDTO addProjectHead(@RequestBody ProjectHeadDTO projectHeadDTO) {
        return toProjectHeadDTO(projectService.saveProjectHead(toProjectHead(projectHeadDTO)));
    }

    @PutMapping("/head")
    public ProjectHeadDTO updateProjectHead(@RequestBody ProjectHeadDTO projectHeadDTO) {
        return toProjectHeadDTO(projectService.updateProjectHead(toProjectHead(projectHeadDTO)));
    }

    @GetMapping("/head/{id}")
    public ProjectHeadDTO findProjectHeadById(@PathVariable Long id) {
        return toProjectHeadDTO(projectService.findById(id));
    }

    @GetMapping("/head")
    public List<ProjectHeadDTO> findAllProjectHeads() {
        return toProjectHeadDTOs(projectService.findAllProjectHead());
    }

    @GetMapping("/head/activityAndTask")
    public List<ProjectHeadDTO> findAllProjectHeadWithActivityAndTask(@RequestParam(value="projectIds", required = false) List<Long> projectIds) {
        return toProjectHeadDTOs(projectService.findAllProjectHeadWithActivityAndTask(projectIds));
    }

    @GetMapping("/head/findAllByProjectHeadId/{id}")
    public List<ProjectHeadDTO> findAllByProjectHeadId(@PathVariable Long id) {
        return toProjectHeadDTOs(projectService.findAllByProjectHeadId(id));
    }

    @GetMapping("/headAndDetails/{registerHeadId}")
    public List<ProjectHeadDTO> findProjectHeadAndDetails(@PathVariable Long registerHeadId) {
        return toProjectHeadDTOs(projectService.findAllByRegisterId(registerHeadId));
    }

    @GetMapping("/findProjectTree/{projectId}")
    public ProjectHeadDTO findProjectTree(@PathVariable Long projectId) {
        return toProjectHeadDTO(projectService.findByIdFetchAll(projectId));
    }

    @DeleteMapping("/head/{id}")
    public ResponseEntity deleteProjectHead(@PathVariable Long id) {
        projectService.deleteProjectHead(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllProjectHeads() {
        projectService.deleteAllProjectHead();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ProjectDetail //////////////////////////////////////
    @PostMapping("/detail")
    public ProjectDetailDTO addProjectDetail(@RequestBody ProjectDetailDTO projectDetailDTO) {
        return toProjectDetailDTO(projectService.saveProjectDetail(toProjectDetail(projectDetailDTO)));
    }

    @PutMapping("/detail")
    public ProjectDetailDTO updateProjectDetail(@RequestBody ProjectDetailDTO projectDetailDTO) {
        ProjectDetail projectDetail = projectDetailRepository.findById(projectDetailDTO.getId()).orElse(null);
        return toProjectDetailDTO(projectDetail == null ? projectDetail :
                projectService.saveProjectDetail(toUpdatedProjectDetail(projectDetail,
                        toProjectDetail(projectDetailDTO))));
    }

    @GetMapping("/detail/{id}")
    public ProjectDetailDTO findProjectDetailById(@PathVariable Long id) {
        return toProjectDetailDTO(projectService.findProjectDetailById(id));
    }

    @GetMapping("/detail")
    public List<ProjectDetailDTO> findAllProjectDetails() {
        return toProjectDetailDTOs(projectService.findAllProjectDetail());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteProjectDetail(@PathVariable Long id) {
        projectService.deleteProjectDetail(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllProjectDetails() {
        projectService.deleteAllProjectDetail();
        return new ResponseEntity(HttpStatus.OK);
    }

    ////Project Partner ///////
    @PostMapping("/partner")
    public ProjectPartnerDTO addProjectPartner(@RequestBody ProjectPartnerDTO projectPartnerDTO) {
        return toProjectPartnerDTO(projectPartnerService.save(toProjectPartner(projectPartnerDTO)));
    }

    @GetMapping("/partner/findAll/{projectId}")
    public List<ProjectPartnerDTO> findAllPartnerByProjectId(@PathVariable Long projectId) {
        return toProjectPartnerDTOs(projectPartnerService.findAllByProjectId(projectId));
    }

    @DeleteMapping("/partner/{id}")
    public ResponseEntity deletePartnerById(@PathVariable Long id) {
        projectPartnerService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //Project Resource Engagement ///////
    @PostMapping("/resourceEngagement")
    public ProjectResourceEngagementDTO addProjectResourceEngagement(@RequestBody ProjectResourceEngagementDTO projectResourceEngagementDTO) {
        return toProjectResourceEngagementDTO(projectResourceEngagementService.save(toProjectResourceEngagement(projectResourceEngagementDTO)));
    }

    @GetMapping("/resourceEngagement/findById/{projectEngagementId}")
    public ProjectResourceEngagementDTO findById(@PathVariable Long projectEngagementId) {
        return toProjectResourceEngagementDTO(projectResourceEngagementService.findById(projectEngagementId));
    }

    @GetMapping("/resourceEngagement/findAllByProjectId/{projectId}")
    public List<ProjectResourceEngagementDTO> findAllByProjectId(@PathVariable Long projectId) {
        return toProjectResourceEngagementDTOs(projectResourceEngagementService.findAllByProjectId(projectId));
    }

    @GetMapping("/resourceEngagement/findAllProjectByLoginId/{loginId}")
    public List<ProjectResourceEngagementDTO> findAllProjectByLoginId(@PathVariable Long loginId) {
        return toProjectResourceEngagementDTOs(projectResourceEngagementService.findAllProjectByResourceId(loginId));
    }

    @GetMapping("/resourceEngagement/findResourceByProjectId/{projectId}")
    public List<ProjectAssociatedResourceDTO> findResourceByProjectId(@PathVariable Long projectId) {
        return projectResourceEngagementService.findResourceByProjectId(projectId);
    }

    @GetMapping("/resourceEngagement/findOneResourceByEngagementId/{projectEngagementId}")
    public ProjectAssociatedResourceDTO findOneResourceByProjectId(@PathVariable Long projectEngagementId) {
        return projectResourceEngagementService.findOneResourceByProjectId(projectEngagementId);
    }

    @GetMapping("/resourceEngagement/findByProjectIdAndResourceId/{projectId}/{resourceId}")
    public List<ProjectResourceEngagement> findByProjectIdAndResourceId(@PathVariable Long projectId, @PathVariable Long resourceId) {
        return projectResourceEngagementService.findByProjectIdAndResourceId(projectId, resourceId);
    }

    @GetMapping("/resourceEngagement/findResourceByTaskId/{taskId}")
    public List<ProjectAssociatedResourceDTO> findResourceByTaskId(@PathVariable Long taskId) {
        return projectResourceEngagementService.findResourceByTaskId(taskId);
    }

    @GetMapping("/resourceEngagement/findTaskByResourceId/{projectId}/{resourceId}")
    public List<ProjectAssociatedResourceDTO> findTaskByResourceId(@PathVariable Long projectId, @PathVariable Long resourceId) {
        return projectResourceEngagementService.findTaskByResourceId(projectId, resourceId);
    }

//    @GetMapping("/resourceEngagement/{employeeId}/{roleId}")
//    public String querytest(@PathVariable Long employeeId,
//                            @PathVariable Long roleId) {
//        return resourceAttendanceLogRepository.hoursByRole(employeeId, roleId);
//    }


    @GetMapping("/resourceEngagement/findByProjectIdAndResourceIdAndEngagementRoleId/{projectId}/{resourceId}/{engagementRoleId}")
    public ProjectResourceEngagementDTO findByProjectIdAndResourceIdAndEngagementRoleId(@PathVariable Long projectId, @PathVariable Long resourceId, @PathVariable Long engagementRoleId) {
        return toProjectResourceEngagementDTO(projectResourceEngagementService.findByProjectIdAndResourceIdAndEngagementRoleId(projectId, resourceId, engagementRoleId));
    }

    @PutMapping("/resourceEngagement")
    public ProjectResourceEngagementDTO updateProjectResourceEngagement(@RequestBody ProjectResourceEngagementDTO projectResourceEngagementDTO) {
        return toProjectResourceEngagementDTO(projectResourceEngagementService.update(toProjectResourceEngagement(projectResourceEngagementDTO)));
    }

    @DeleteMapping("/resourceEngagement/delete/{id}")
    public ResponseEntity deleteByResourceEngagementId(@PathVariable Long id) {
        projectResourceEngagementService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    ///Project Inventory ////////
    @PostMapping("/inventory")
    public ProjectInventoryDTO addProjectInventory(@RequestBody ProjectInventoryDTO projectInventoryDTO) {
        return toProjectInventoryDTO(projectInventoryService.save(toProjectInventory(projectInventoryDTO)));
    }

    @PutMapping("/inventory")
    public ProjectInventoryDTO updateProjectInventoryAndSerial(@RequestBody ProjectInventoryDTO projectInventoryDTO) {
        ProjectInventoryDTO projectInventoryDTO1 = toProjectInventoryDTO(projectInventoryService.update(toProjectInventory(projectInventoryDTO)));
        return projectInventoryDTO1;
    }

    @GetMapping("/inventory/findAllByProjectId/{projectId}")
    public List<ProjectInventoryDTO> findInventoriesByProjectId(@PathVariable Long projectId) {
        return toProjectInventoryDTOs(projectInventoryService.findAllByProjectId(projectId));
    }

    @DeleteMapping("/inventory/delete/{id}")
    public ResponseEntity deleteByInventoryId(@PathVariable Long id) {
        projectInventoryService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    //Project Inventory Serial ////////
    @PutMapping("/inventorySerial/update")
    public List<ProjectInventorySerialDTO> updateProjectInventorySerials(@RequestBody List<ProjectInventorySerialDTO> projectInventorySerialDTOs) {
        return toProjectInventorySerialDTOs(projectInventoryService.updateInventorySerials(toProjectInventorySerials(projectInventorySerialDTOs)));
    }

    @GetMapping("/inventorySerial/findAllByInventoryId/{projectInventoryId}/{page}/{pageSize}/{sort}")
    public PagedProjectInventorySerialDTO findAllByInventoryId(@PathVariable Long projectInventoryId,
                                                               @PathVariable("page") int pageNumber,
                                                               @PathVariable("pageSize") Integer pageSize,
                                                               @PathVariable("sort") String sort) {
        return projectInventoryService.findAllByProjectInventory(projectInventoryId, pageNumber, pageSize, sort);
    }

    @PostMapping("/inventorySerial/delete")
    public ResponseEntity deleteProjectInventorySerials(@RequestBody List<ProjectInventorySerialDTO> projectInventorySerialDTOs) {
        projectInventoryService.deleteInventorySerials(toProjectInventorySerials(projectInventorySerialDTOs));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/inventorySerial/delete/{id}")
    public ResponseEntity deleteProjectInventorySerialsById(@PathVariable Long id) {
        projectInventoryService.deleteInventorySerialsById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    ///Engagement Rate Group////////
    @GetMapping("/engagementRateGroups/findAll")
    public List<EngagementRateGroupsDTO> findAllEngagementRateGroups() {
        return toEngagementRateGroupsDTOs(engagementRateGroupsService.findAll());
    }

    @GetMapping("/engagementRateGroups/findAllByProjectId/{projectId}")
    public List<EngagementRateGroupsDTO> findAllEngagementRateGroupsByProjectId(@PathVariable Long projectId) {
        return toEngagementRateGroupsDTOs(engagementRateGroupsService.findAllByProjectId(projectId));
    }

    //Project Site////////////////////////////////////
    @PostMapping("/site")
    public ProjectSiteDTO addProjectSite(@RequestBody ProjectSiteDTO projectSiteDTO) {
        return toProjectSiteDTO(projectSiteService.save(toProjectSite(projectSiteDTO)));
    }

    @GetMapping("/site/findAllByProjectId/{projectId}")
    public List<ProjectSiteDTO> findAllProjectSiteByProjectId(@PathVariable Long projectId) {
        return toProjectSiteDTOs(projectSiteService.findAllByProjectId(projectId));
    }

    @GetMapping("/site/findAll")
    public List<ProjectSiteDTO> findAllProjectSite() {
        return toProjectSiteDTOs(projectSiteService.findAll());
    }

    @PutMapping("/site")
    public ProjectSiteDTO updateProjectSite(@RequestBody ProjectSiteDTO projectSiteDTO) {
        return toProjectSiteDTO(projectSiteService.update(toProjectSite(projectSiteDTO)));
    }

    @DeleteMapping("/site/{projectId}/{siteId}")
    public ResponseEntity deleteSiteByProjectIdAndSiteId(@PathVariable Long projectId, @PathVariable Long siteId) {
        projectSiteService.deleteByProjectIdAndSiteId(projectId, siteId);
        return new ResponseEntity(HttpStatus.OK);
    }

    //Download Serial NumbersTemplate //////
    @GetMapping("/exportTemplate/serialNumbers/{registerId}")
    public @ResponseBody
    ObjectNode getFileV1(HttpServletRequest request, HttpServletResponse response, @PathVariable Long registerId) {
        try {

            List<RegisterDetail> registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(registerId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            OutputStreamWriter osNew = new OutputStreamWriter(stream, "UTF-8");
            PrintWriter writer1 = new PrintWriter(osNew);
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer1, CsvPreference.EXCEL_PREFERENCE);

            String[] csvHeader = new String[registerDetails.size() + 1];
            csvHeader[0] = "Pallet Number";
            int headerCounter = 1;
            for (RegisterDetail rd : registerDetails) {
                MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(rd.getMeasureCodeId());
                csvHeader[headerCounter] = measureDefinitionTenantDb.getMeasure();
                headerCounter++;
            }
            LOGGER.info(csvHeader.toString());

            csvWriter.writeHeader(csvHeader);
            writer1.flush();
            csvWriter.flush();
            stream.close();
            String blobUrl = "";
            byte[] byteArray = stream.toByteArray();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream(byteArray.length)) {
                os.write(byteArray, 0, byteArray.length);
                try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                    blobUrl = storageService.uploadInputStream(is, (long) os.size(), appProfile,
                            "tenant/" + utility.getCompKey()
                                    + "/project/serials",
                            "SerialNumber-" + registerId + ".csv", utility.getCompKey(), false);
                }
            }
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", blobUrl);
            return messageJson;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", "Failed to download serial number template file.");
            return messageJson;
        }
    }

    //Download Pallet Template //////
    @GetMapping("/inventorySerial/exportTemplate/{assetId}")
    public @ResponseBody
    ObjectNode inventorySerialTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable Long assetId) {
        try {

            AssetHead assetHead = assetHeadRepository.findById(assetId).get();
            List<RegisterDetail> registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(assetHead.getRegisterId());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            OutputStreamWriter osNew = new OutputStreamWriter(stream, "UTF-8");
            PrintWriter writer1 = new PrintWriter(osNew);
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer1, CsvPreference.EXCEL_PREFERENCE);

            //String[] csvHeader = new String[registerDetails.size()];
            String[] csvHeader = new String[registerDetails.size() + 1];
            csvHeader[0] = "Pallet Number";
            int headerCounter = 1;
            for (RegisterDetail rd : registerDetails) {
                MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(rd.getMeasureCodeId());
                csvHeader[headerCounter] = measureDefinitionTenantDb.getMeasure();
                headerCounter++;
            }
            LOGGER.info(csvHeader.toString());

            csvWriter.writeHeader(csvHeader);
            writer1.flush();
            csvWriter.flush();
            stream.close();
            String blobUrl = "";
            byte[] byteArray = stream.toByteArray();
            blobUrl = Utility.uploadToStorage(storageService, byteArray, appProfile, "tenant/" + utility.getCompKey()
                            + "/project/inventorySerial", "Pallets SerialNumber-" + assetId + ".csv",
                    utility.getCompKey(), false);

            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", blobUrl);
            return messageJson;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", "Failed to download serial number template file.");
            return messageJson;
        }
    }

    // Check-In Check-Out ////////
    @PostMapping("/checkIn")
    public List<ResourceAttendanceLogDTO> addCheckIn(@RequestBody List<CheckInCheckOutDTO> checkOuts) {
        return toResourceAttendanceLogDTOs(resourceAttendanceLogService.addCheckIn(checkOuts));
    }

    @GetMapping("/currentDateAttendance/{employeeId}/{taskId}")
    public List<ResourceAttendanceLog> currentDateAttendance(@PathVariable Long employeeId, @PathVariable Long taskId) {
        List<ResourceAttendanceLog> resourceAttendanceLogs = resourceAttendanceLogService.findByEmployeeIdAndTaskIdAndWorkDate(employeeId, taskId, LocalDateTime.now().toLocalDate().toString());
        return resourceAttendanceLogs;
//        return ResourceAttendanceLogMapper.toResourceAttendanceLogDTOs(resourceAttendanceLogService.addCheckIn(checkOuts));
    }

    @PostMapping("/checkOut")
    public List<ResourceAttendanceLogDTO> addCheckOut(@RequestBody List<CheckInCheckOutDTO> checkOuts) {
        return toResourceAttendanceLogDTOs(resourceAttendanceLogService.addCheckOut(checkOuts));
    }

    ///export csv for serial number
    @PostMapping("/download/serialNumbers/{assetId}/{projectId}")
    public @ResponseBody
    ObjectNode downloadSerialNumbers(@PathVariable("assetId") Long assetId, @PathVariable("projectId") Long projectId) throws Exception {

        try {

            String blobUrl = assetBlockDetailService.getSerialNumbersForCSVExport(assetId, projectId);

            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", blobUrl);
            return messageJson;

        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", "Failed to export serial numbers file.");
            return messageJson;
        }
    }

    //////Project Dependencies
    @PostMapping("/projectDependencies")
    public List<ProjectDependenciesDTO> addProjectDependencies(@RequestBody List<ProjectDependenciesDTO> projectDependenciesDTO) {
        return toProjectDependenciesDTOs(projectService.saveProjectDependencies(toProjectDependenciesList(projectDependenciesDTO)));
    }

    @PutMapping("/projectDependencies")
    public List<ProjectDependenciesDTO> updateProjectDependencies(@RequestBody List<ProjectDependenciesDTO> projectDependenciesDTOs) {
        return toProjectDependenciesDTOs(projectService.updateProjectDependencies(toProjectDependenciesList(projectDependenciesDTOs)));
    }

    @GetMapping("/projectDependencies/findAllProjectDependenciesById/{id}/{fieldName}")
    public List<ProjectHeadDTO> findAllProjectDependenciesById(@PathVariable Long id, @PathVariable String fieldName) {
        //return toProjectDependenciesDTOs(projectService.findAllProjectDependenciesByProjectId(projectId));
        return toProjectHeadDTOs(projectService.findAllProjectDependenciesById(id, fieldName));
    }

    @PostMapping("/projectDependencies/unLinkedDependencies")
    public List<ProjectHeadDTO> unLinkedDependencies(@RequestBody List<LinkedUnLinkedDependenciesDTO> dependenciesDTOS) {
        return toProjectHeadDTOs(projectService.unLinkedDependencies(dependenciesDTOS));
    }

    @GetMapping("/projectDependencies/findAllActivitiesAndTasks/{projectId}")
    public ProjectDependenciesViewListDTO findAllActivitiesAndTasks(@PathVariable Long projectId) {
        return projectService.findAllActivitiesAndTasks(projectId);
    }

    @DeleteMapping("/projectDependencies/delete/{id}")
    public ResponseEntity deleteByProjectDependenciesId(@PathVariable Long id) {
        projectService.deleteByProjectDependenciesId(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/ganttChart")
    public List<ProjectHeadChartDTO> ganttChart() {
        return projectService.ganttChart();
    }

    @PostMapping("/checkDateValidation")
    public ProjectDatesValidationDTO checkDateValidation(@RequestBody ProjectDatesValidationDTO projectDatesValidationDTO) {
        return projectService.checkDateValidation(projectDatesValidationDTO);
    }

    @PutMapping("/updateDateValidation")
    public ProjectDatesValidationDTO updateDateValidation(@RequestBody ProjectDatesValidationDTO projectDatesValidationDTO) {
        return projectService.updateDateValidation(projectDatesValidationDTO);
    }

    @GetMapping("/projectSummary/findById/{projectId}")
    public ProjectSummaryViewDTO projectSummaryFindById(@PathVariable Long projectId) {
        return projectService.projectSummaryFindById(projectId);
    }
}
