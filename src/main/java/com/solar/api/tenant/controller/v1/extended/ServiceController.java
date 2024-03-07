package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.service.*;
import com.solar.api.tenant.model.extended.service.*;
import com.solar.api.tenant.repository.service.*;
import com.solar.api.tenant.service.extended.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.service.ServiceMapper.*;
import static com.solar.api.tenant.mapper.extended.service.WorkOrderMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ServiceController")
@RequestMapping(value = "/service")
public class ServiceController {

    @Autowired
    private ServiceHeadService serviceHeadService;
    @Autowired
    private ServiceDetailService serviceDetailService;
    @Autowired
    private ServiceResourcesService serviceResourcesService;
    @Autowired
    private WorkOrderHeadService workOrderHeadService;
    @Autowired
    private WorkOrderDetailService workOrderDetailService;
    @Autowired
    private WorkOrderCommsService workOrderCommsService;
    @Autowired
    private ServiceHeadRepository serviceHeadRepository;
    @Autowired
    private ServiceDetailRepository serviceDetailRepository;
    @Autowired
    private ServiceResourcesRepository serviceResourcesRepository;
    @Autowired
    private WorkOrderHeadRepository workOrderHeadRepository;
    @Autowired
    private WorkOrderDetailRepository workOrderDetailRepository;
    @Autowired
    private WorkOrderCommsRepository workOrderCommsRepository;

    // ServiceHead ////////////////////////////////////////
    @PostMapping("/head")
    public ServiceHeadDTO addServiceHead(@RequestBody ServiceHeadDTO serviceHeadDTO) {
        return toServiceHeadDTO(serviceHeadService.save(toServiceHead(serviceHeadDTO)));
    }

    @PutMapping("/head")
    public ServiceHeadDTO updateServiceHead(@RequestBody ServiceHeadDTO serviceHeadDTO) {
        ServiceHead serviceHead = serviceHeadRepository.findById(serviceHeadDTO.getServiceId()).orElse(null);
        return toServiceHeadDTO(serviceHead == null ? serviceHead :
                serviceHeadService.save(toUpdatedServiceHead(serviceHead, toServiceHead(serviceHeadDTO))));
    }

    @GetMapping("/head/{id}")
    public ServiceHeadDTO findServiceHeadById(@PathVariable Long id) {
        return toServiceHeadDTO(serviceHeadService.findById(id));
    }

    @GetMapping("/head")
    public List<ServiceHeadDTO> findAllServiceHeads() {
        return toServiceHeadDTOs(serviceHeadService.findAll());
    }

    @DeleteMapping("/head/{id}")
    public ResponseEntity deleteServiceHead(@PathVariable Long id) {
        serviceHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllServiceHeads() {
        serviceHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ServiceDetail ////////////////////////////////////////
    @PostMapping("/detail")
    public ServiceDetailDTO addServiceDetail(@RequestBody ServiceDetailDTO serviceDetailDTO) {
        return toServiceDetailDTO(serviceDetailService.save(toServiceDetail(serviceDetailDTO)));
    }

    @PutMapping("/detail")
    public ServiceDetailDTO updateServiceDetail(@RequestBody ServiceDetailDTO serviceDetailDTO) {
        ServiceDetail serviceDetail = serviceDetailRepository.findById(serviceDetailDTO.getId()).orElse(null);
        return toServiceDetailDTO(serviceDetail == null ? serviceDetail :
                serviceDetailService.save(toUpdatedServiceDetail(serviceDetail, toServiceDetail(serviceDetailDTO))));
    }

    @GetMapping("/detail/{id}")
    public ServiceDetailDTO findServiceDetailById(@PathVariable Long id) {
        return toServiceDetailDTO(serviceDetailService.findById(id));
    }

    @GetMapping("/detail")
    public List<ServiceDetailDTO> findAllServiceDetails() {
        return toServiceDetailDTOs(serviceDetailService.findAll());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteServiceDetail(@PathVariable Long id) {
        serviceDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllServiceDetails() {
        serviceDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ServiceResources ////////////////////////////////////////
    @PostMapping("/resource")
    public ServiceResourcesDTO addServiceResources(@RequestBody ServiceResourcesDTO serviceResourcesDTO) {
        return toServiceResourcesDTO(serviceResourcesService.save(toServiceResources(serviceResourcesDTO)));
    }

    @PutMapping("/resource")
    public ServiceResourcesDTO updateServiceResources(@RequestBody ServiceResourcesDTO serviceResourcesDTO) {
        ServiceResources serviceResources =
                serviceResourcesRepository.findById(serviceResourcesDTO.getId()).orElse(null);
        return toServiceResourcesDTO(serviceResources == null ? serviceResources :
                serviceResourcesService.save(toUpdatedServiceResources(serviceResources,
                        toServiceResources(serviceResourcesDTO))));
    }

    @GetMapping("/resource/{id}")
    public ServiceResourcesDTO findServiceResourcesById(@PathVariable Long id) {
        return toServiceResourcesDTO(serviceResourcesService.findById(id));
    }

    @GetMapping("/resource")
    public List<ServiceResourcesDTO> findAllServiceResources() {
        return toServiceResourcesDTOs(serviceResourcesService.findAll());
    }

    @DeleteMapping("/resource/{id}")
    public ResponseEntity deleteServiceResources(@PathVariable Long id) {
        serviceResourcesService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/resource")
    public ResponseEntity deleteAllServiceResources() {
        serviceResourcesService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // WorkOrderHead ////////////////////////////////////////
    @PostMapping("/workOrderHead")
    public WorkOrderHeadDTO addWorkOrderHead(@RequestBody WorkOrderHeadDTO workOrderHeadDTO) {
        return toWorkOrderHeadDTO(workOrderHeadService.save(toWorkOrderHead(workOrderHeadDTO)));
    }

    @PutMapping("/workOrderHead")
    public WorkOrderHeadDTO updateWorkOrderHead(@RequestBody WorkOrderHeadDTO workOrderHeadDTO) {
        WorkOrderHead workOrderhead = workOrderHeadRepository.findById(workOrderHeadDTO.getId()).orElse(null);
        return toWorkOrderHeadDTO(workOrderhead == null ? workOrderhead :
                workOrderHeadService.save(toUpdatedWorkOrderHead(workOrderhead, toWorkOrderHead(workOrderHeadDTO))));
    }

    @GetMapping("/workOrderHead/{id}")
    public WorkOrderHeadDTO findWorkOrderHeadById(@PathVariable Long id) {
        return toWorkOrderHeadDTO(workOrderHeadService.findById(id));
    }

    @GetMapping("/workOrderHead")
    public List<WorkOrderHeadDTO> findAllWorkOrderHeads() {
        return toWorkOrderHeadDTOs(workOrderHeadService.findAll());
    }

    @DeleteMapping("/workOrderHead/{id}")
    public ResponseEntity deleteWorkOrderHead(@PathVariable Long id) {
        workOrderHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/workOrderHead")
    public ResponseEntity deleteAllWorkOrderHeads() {
        workOrderHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // WorkOrderDetail ////////////////////////////////////////
    @PostMapping("/workOrderDetail")
    public WorkOrderDetailDTO addWorkOrderDetail(@RequestBody WorkOrderDetailDTO workOrderDetailDTO) {
        WorkOrderDetail workOrderDetail = workOrderDetailRepository.findById(workOrderDetailDTO.getId()).orElse(null);
        return toWorkOrderDetailDTO(workOrderDetail == null ? workOrderDetail :
                workOrderDetailService.save(toUpdatedWorkOrderDetail(workOrderDetail,
                        toWorkOrderDetail(workOrderDetailDTO))));
    }

    @PutMapping("/workOrderDetail")
    public WorkOrderDetailDTO updateWorkOrderDetail(@RequestBody WorkOrderDetailDTO workOrderDetailDTO) {
        return toWorkOrderDetailDTO(workOrderDetailService.save(toWorkOrderDetail(workOrderDetailDTO)));
    }

    @GetMapping("/workOrderDetail/{id}")
    public WorkOrderDetailDTO findWorkOrderDetailById(@PathVariable Long id) {
        return toWorkOrderDetailDTO(workOrderDetailService.findById(id));
    }

    @GetMapping("/workOrderDetail")
    public List<WorkOrderDetailDTO> findAllWorkOrderDetails() {
        return toWorkOrderDetailDTOs(workOrderDetailService.findAll());
    }

    @DeleteMapping("/workOrderDetail/{id}")
    public ResponseEntity deleteWorkOrderDetail(@PathVariable Long id) {
        workOrderDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/workOrderDetail")
    public ResponseEntity deleteAllWorkOrderDetails() {
        workOrderDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // WorkOrderComms ////////////////////////////////////////
    @PostMapping("/workOrderComms")
    public WorkOrderCommsDTO addWorkOrderComms(@RequestBody WorkOrderCommsDTO workOrderCommsDTO) {
        return toWorkOrderCommsDTO(workOrderCommsService.save(toWorkOrderComms(workOrderCommsDTO)));
    }

    @PutMapping("/workOrderComms")
    public WorkOrderCommsDTO updateWorkOrderComms(@RequestBody WorkOrderCommsDTO workOrderCommsDTO) {
        WorkOrderComms workOrderComms = workOrderCommsRepository.findById(workOrderCommsDTO.getId()).orElse(null);
        return toWorkOrderCommsDTO(workOrderComms == null ? workOrderComms :
                workOrderCommsService.save(toUpdatedWorkOrderComms(workOrderComms,
                        toWorkOrderComms(workOrderCommsDTO))));
    }

    @GetMapping("/workOrderComms/{id}")
    public WorkOrderCommsDTO findWorkOrderCommsById(@PathVariable Long id) {
        return toWorkOrderCommsDTO(workOrderCommsService.findById(id));
    }

    @GetMapping("/workOrderComms")
    public List<WorkOrderCommsDTO> findAllWorkOrderComms() {
        return toWorkOrderCommsDTOs(workOrderCommsService.findAll());
    }

    @DeleteMapping("/workOrderComms/{id}")
    public ResponseEntity deleteWorkOrderComms(@PathVariable Long id) {
        workOrderCommsService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/workOrderComms")
    public ResponseEntity deleteAllWorkOrderComms() {
        workOrderCommsService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
