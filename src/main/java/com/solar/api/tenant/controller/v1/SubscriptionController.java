package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.mongo.response.subscription.VariantDTO;
import com.solar.api.tenant.mapper.subscription.SubscriptionTemplate;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMaintenanceDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionRateCodeDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.SubscriptionCountDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping.CustomerSubscriptionMappingDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetailDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHeadDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRatesDerivedDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionsListView;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.process.subscription.rollover.SubscriptionRollover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper.*;
import static com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping.CustomerSubscriptionMappingMapper.*;
import static com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateTypeMatrixMapper.*;
import static com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SubscriptionController")
@RequestMapping(value = "/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionRollover subscriptionRollover;

    SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // SubscriptionType ////////////////////////////////////////////////////////
    @PostMapping("/subscriptionType")
    public SubscriptionTypeDTO addSubscriptionType(@RequestBody SubscriptionTypeDTO subscriptionTypeDTO) {
        return toSubscriptionTypeDTO(subscriptionService.addOrUpdateSubscriptionType(toSubscriptionType(subscriptionTypeDTO)));
    }

    @PostMapping("/subscriptionTypes")
    public List<SubscriptionTypeDTO> addSubscriptionTypes(@RequestBody List<SubscriptionTypeDTO> subscriptionTypeDTOs) {
        return toSubscriptionTypeDTOs(subscriptionService.addSubscriptionTypes(toSubscriptionTypes(subscriptionTypeDTOs)));
    }

    @PutMapping("/subscriptionType")
    public SubscriptionTypeDTO updateSubscriptionType(@RequestBody SubscriptionTypeDTO subscriptionTypeDTO) {
        return toSubscriptionTypeDTO(subscriptionService.addOrUpdateSubscriptionType(toSubscriptionType(subscriptionTypeDTO)));
    }

    @GetMapping("/subscriptionType/{id}")
    public SubscriptionTypeDTO findSubscriptionTypeById(@PathVariable Long id) {
        return toSubscriptionTypeDTO(subscriptionService.findSubscriptionTypeById(id));
    }

    @GetMapping("/subscriptionType")
    public List<SubscriptionTypeDTO> findAllSubscriptionTypes(@RequestParam(value = "status", required = false, defaultValue = "active") String status) {
        return toSubscriptionTypeDTOs(subscriptionService.findAllSubscriptionTypes(status));
    }

    @GetMapping("/subscriptionGroupsTypes")
    public List<SubscriptionTypeDTO> getSubscriptionPrimaryGroupsAndTypes() {
        return subscriptionService.findAllSubscriptionTypesWithPrimaryGroup();
    }

    @DeleteMapping("/subscriptionType/{id}")
    public ResponseEntity deleteSubscriptionType(@PathVariable Long id) {
        subscriptionService.deleteSubscriptionType(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/subscriptionType")
    public ResponseEntity deleteAllSubscriptionTypes() {
        subscriptionService.deleteAllSubscriptionTypes();
        return new ResponseEntity(HttpStatus.OK);
    }

    // SubscriptionRateMatrixHead /////////////////////////////////////////////////
    @PostMapping("/subscriptionRateMatrixHead")
    public SubscriptionRateMatrixHeadDTO addSubscriptionRateMatrixHead(@RequestBody SubscriptionRateMatrixHeadDTO subscriptionRateMatrixHeadDTO) {
        return toSubscriptionRateMatrixHeadDTO(subscriptionService.addOrUpdateSubscriptionRateMatrixHead(toSubscriptionRateMatrixHead(subscriptionRateMatrixHeadDTO)));
    }

    @PostMapping("/subscriptionRateMatrixHeads")
    public List<SubscriptionRateMatrixHeadDTO> addSubscriptionRateMatrixHeads(@RequestBody List<SubscriptionRateMatrixHeadDTO> subscriptionRateMatrixHeadDTOs) {
        return toSubscriptionRateMatrixHeadDTOs(subscriptionService.addSubscriptionRateMatrixHeads(toSubscriptionRateMatrixHeads(subscriptionRateMatrixHeadDTOs)));
    }

    @PutMapping("/subscriptionRateMatrixHead")
    public SubscriptionRateMatrixHeadDTO updateSubscriptionRateMatrixHead(@RequestBody SubscriptionRateMatrixHeadDTO subscriptionRateMatrixHeadDTO) {
        return toSubscriptionRateMatrixHeadDTO(subscriptionService.addOrUpdateSubscriptionRateMatrixHead(toSubscriptionRateMatrixHead(subscriptionRateMatrixHeadDTO)));
    }

    @GetMapping("/subscriptionRateMatrixHead/{subscriptionRateMatrixId}")
    public SubscriptionRateMatrixHeadDTO findSubscriptionRateMatrixHeadById(@PathVariable Long subscriptionRateMatrixId) {
        return toSubscriptionRateMatrixHeadDTO(subscriptionService.findSubscriptionRateMatrixHeadById(subscriptionRateMatrixId));
    }

    @GetMapping("/subscriptionRateMatrixHead")
    public List<SubscriptionRateMatrixHeadDTO> findAllSubscriptionRateMatrixHeads() {
        return toSubscriptionRateMatrixHeadDTOs(subscriptionService.findAllSubscriptionRateMatrixHeadsFetchDetails());
    }

    @GetMapping("/subscriptionRateMatrixHead/subscriptionCode/{subscriptionCode}")
    public List<SubscriptionRateMatrixHeadDTO> findAllSubscriptionRateMatrixHeads(@PathVariable String subscriptionCode) {
        return toSubscriptionRateMatrixHeadDTOs(subscriptionService.findSubscriptionRateMatrixHeadBySubscriptionCodeAndActive(subscriptionCode, true));
    }

    @DeleteMapping("/subscriptionRateMatrixHead/{subscriptionRateMatrixId}")
    public ResponseEntity deleteSubscriptionRateMatrixHead(@PathVariable Long subscriptionRateMatrixId) {
        subscriptionService.deleteSubscriptionRateMatrixHead(subscriptionRateMatrixId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/subscriptionRateMatrixHead")
    public ResponseEntity deleteAllSubscriptionRateMatrixHeads() {
        subscriptionService.deleteAllSubscriptionRateMatrixHeads();
        return new ResponseEntity(HttpStatus.OK);
    }

    // SubscriptionRateMatrixDetail /////////////////////////////////////////////////
    @PostMapping("/subscriptionRateMatrixDetail/add")
    public SubscriptionRateMatrixDetailDTO addSubscriptionRateMatrix(@RequestBody SubscriptionRateMatrixDetailDTO subscriptionRateMatrixDetailDTO) {
        return toSubscriptionRateMatrixDTO(subscriptionService.addOrUpdateSubscriptionRateMatrix(toSubscriptionRateMatrix(subscriptionRateMatrixDetailDTO)));
    }

    @PostMapping("/subscriptionRateMatrixDetails")
    public List<SubscriptionRateMatrixDetailDTO> addSubscriptionRateMatrices(@RequestBody List<SubscriptionRateMatrixDetailDTO> subscriptionRateMatrixDetailDTOs) {
        return toSubscriptionRateMatrixDetailDTOs(subscriptionService.add(toSubscriptionRateMatrixDetails(subscriptionRateMatrixDetailDTOs)));
    }

    @PutMapping("/subscriptionRateMatrixDetail")
    public SubscriptionRateMatrixDetailDTO updateSubscriptionRateMatrix(@RequestBody SubscriptionRateMatrixDetailDTO subscriptionRateMatrixDetailDTO) {
        return toSubscriptionRateMatrixDTO(subscriptionService.addOrUpdateSubscriptionRateMatrix(toSubscriptionRateMatrix(subscriptionRateMatrixDetailDTO)));
    }

    /**
     * For
     * PaymentDownload
     *
     * @return
     */
    @GetMapping("/findDefaultValueInSubscriptionRateMatrixDetail")
    public List<SubscriptionRateMatrixDetailDTO> findDefaultValueForPaymentDownload() {
        return toSubscriptionRateMatrixDetailDTOs(subscriptionService.findDefaultValueForPaymentDownload());
    }

    @GetMapping("/subscriptionRateMatrixDetail/{id}")
    public SubscriptionRateMatrixDetailDTO findSubscriptionRateMatrixDetailById(@PathVariable Long id) {
        return toSubscriptionRateMatrixDTO(subscriptionService.findSubscriptionRateMatrixDetailById(id));
    }

    @GetMapping("/subscriptionRateMatrixDetail/head/{subscriptionRateMatrixId}")
    public List<SubscriptionRateMatrixDetailDTO> findBySubscriptionRateMatrixId(@PathVariable Long subscriptionRateMatrixId) {
        return toSubscriptionRateMatrixDetailDTOs(subscriptionService.findBySubscriptionRateMatrixId(subscriptionRateMatrixId));
    }

    @GetMapping("/subscriptionRateMatrixHead/getMeasure/{subscriptionTemplate}")
    public ResponseEntity<SubscriptionRateMatrixHeadDTO> findSubscriptionRateMatrixHeadBySubscriptionTemplate(@PathVariable String subscriptionTemplate) {
        SubscriptionRateMatrixHead headData =
                subscriptionService.findSubscriptionRateMatrixHeadBySubscriptionTemplate(subscriptionTemplate);

//        if (userData != null) {
//
//        } else {
//            return new ResponseEntity<>(toUserDTO(userData), HttpStatus.OK);
//        }
//        UniqueResetLink uniqueResetLink = uniqueResetLinkService.save(email);
//        User user = toUpdatedUser(userData, toUser(userDto));
//        if (userDto.getPassword() != null) {
//            user.setPassword(encoder.encode(userDto.getPassword()));
//        }
//        return new ResponseEntity<>(toUserDTO(userService.saveOrUpdate(user)), HttpStatus.OK);

        return new ResponseEntity<>(toSubscriptionRateMatrixHeadDTO(headData), HttpStatus.OK);
    }

    @GetMapping("/subscriptionRateMatrixDetail")
    public List<SubscriptionRateMatrixDetailDTO> findAllSubscriptionRateMatrixDetails() {
        return toSubscriptionRateMatrixDetailDTOs(subscriptionService.findAllSubscriptionRateMatrixDetails());
    }

    @GetMapping("/findAllSubscriptionRateMatrixDetailsByRateCode/{rateCode}")
    public List<SubscriptionRateMatrixDetailDTO> findAllSubscriptionRateMatrixDetailsByRateCode(@PathVariable String rateCode) {
        return toSubscriptionRateMatrixDetailDTOs(subscriptionService.findAllSubscriptionRateMatrixDetailsByRateCode(rateCode));
    }

    @DeleteMapping("/subscriptionRateMatrixDetail/delete/{subscriptionRateMatrixId}")
    public ResponseEntity deleteSubscriptionRateMatrix(@PathVariable Long subscriptionRateMatrixId) {
        subscriptionService.deleteSubscriptionRateMatrixDetail(subscriptionRateMatrixId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/subscriptionRateMatrixDetail")
    public ResponseEntity deleteAllSubscriptionRateMatrices() {
        subscriptionService.deleteAllSubscriptionRateMatrices();
        return new ResponseEntity(HttpStatus.OK);
    }

    // CustomerSubscriptionMapping /////////////////////////////////////////////////
    @PostMapping("/customerSubscriptionMapping")
    public CustomerSubscriptionMappingDTO addCustomerSubscriptionMapping(@RequestBody CustomerSubscriptionMappingDTO customerSubscriptionMappingDTO) {
        return toCustomerSubscriptionMappingDTO(subscriptionService.addOrUpdateCustomerSubscriptionMapping(toCustomerSubscriptionMapping(customerSubscriptionMappingDTO)));
    }

    @PutMapping("/customerSubscriptionMapping")
    public CustomerSubscriptionMappingDTO updateCustomerSubscriptionMapping(@RequestBody CustomerSubscriptionMappingDTO customerSubscriptionMappingDTO) {
        return toCustomerSubscriptionMappingDTO(subscriptionService.addOrUpdateCustomerSubscriptionMapping(toCustomerSubscriptionMapping(customerSubscriptionMappingDTO)));
    }

    @GetMapping("/customerSubscriptionMapping/{id}")
    public CustomerSubscriptionMappingDTO findCustomerSubscriptionMappingById(@PathVariable Long id) {
        return toCustomerSubscriptionMappingDTO(subscriptionService.findCustomerSubscriptionMappingById(id));
    }

    @GetMapping("/customerSubscriptionMapping")
    public List<CustomerSubscriptionMappingDTO> findAllCustomerSubscriptionMappings() {
        return toCustomerSubscriptionMappingDTOs(subscriptionService.findAllCustomerSubscriptionMappings());
    }

    @GetMapping("/customerSubscriptionMapping/{rateCode}")
    public List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByRateCode(@PathVariable String rateCode) {
        return subscriptionService.findCustomerSubscriptionMappingByRateCode(rateCode);
    }

    @GetMapping("/rollover/{id}")
    public void rollover(@PathVariable String id,
                         @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws ParseException {
        subscriptionRollover.rollover(id, null, isLegacy);
    }

    @GetMapping("/rollover")
    public void rolloverAll(@RequestParam(value = "compKey", required = true) Long isLegacy) throws ParseException {
        subscriptionRollover.rollover(isLegacy);
    }

    @DeleteMapping("/customerSubscriptionMapping/{id}")
    public ResponseEntity deleteCustomerSubscriptionMapping(@PathVariable Long id) {
        subscriptionService.deleteCustomerSubscriptionMapping(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/customerSubscriptionMapping")
    public ResponseEntity deleteCustomerSubscriptionMappings() {
        subscriptionService.deleteAllCustomerSubscriptionMappings();
        return new ResponseEntity(HttpStatus.OK);
    }

    // CustomerSubscription /////////////////////////////////////////////////
    @PostMapping("/customerSubscription")
    public CustomerSubscriptionDTO addCustomerSubscription(@RequestBody CustomerSubscriptionDTO customerSubscriptionDTO,
                                                           @RequestParam(value = "isSubsActive", required =
                                                                   false, defaultValue = "false") boolean isSubsActive,
                                                           @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        customerSubscriptionDTO.getCustomerSubscriptionMappings().forEach(customerSubscriptionMappingDTO -> {
            customerSubscriptionMappingDTO.setId(null);
        });
        return toCustomerSubscriptionDTO(subscriptionService.addOrUpdateCustomerSubscription(toCustomerSubscription(customerSubscriptionDTO), isSubsActive, isLegacy));
    }

    @PutMapping("/customerSubscription")
    public CustomerSubscriptionDTO updateCustomerSubscription(@RequestBody CustomerSubscriptionDTO customerSubscriptionDTO,
                                                              @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
//        return toCustomerSubscriptionDTO(subscriptionService.addOrUpdateCustomerSubscription(toCustomerSubscription
//        (customerSubscriptionDTO)));
        CustomerSubscription cs =
                subscriptionService.addOrUpdateCustomerSubscription(toCustomerSubscription(customerSubscriptionDTO), false, isLegacy);
        return toCustomerSubscriptionDTO(subscriptionService.findByIdFetchCustomerSubscriptionMappings(cs.getId()));
    }

    @PreAuthorize("checkSubscriptionAccess(#id)")
    @GetMapping("/customerSubscription/{id}")
    public CustomerSubscriptionDTO findCustomerSubscriptionById(@PathVariable Long id) {
        return toCustomerSubscriptionDTO(subscriptionService.findCustomerSubscriptionById(id));
    }

    @GetMapping("/customerSubscription/user/{userId}")
    public List<CustomerSubscriptionDTO> findCustomerSubscriptionByUserId(@PathVariable Long userId) {
        return toCustomerSubscriptionDTOs(subscriptionService.findCustomerSubscriptionByUserAccount(userId));
    }

    @GetMapping("/customerSubscription/list/{userId}")
    public List<SubscriptionTemplate> listCustomerSubscriptionByUserId(@PathVariable Long userId) {
        return subscriptionService.listCustomerSubscriptionByUserAccount(userId);
    }

    @GetMapping("/customerSubscription/findActiveBySubscriptionRateMatrixId/{id}")
    public List<CustomerSubscription> findActiveBySubscriptionRateMatrixId(@PathVariable Long id) {
        return subscriptionService.findActiveBySubscriptionRateMatrixId(id);
    }

    @GetMapping("/customerSubscription/findForTrueUp/{id}")
    public List<Long> findForTrueUp(@PathVariable Long id) {
//        List<CustomerSubscription> customerSubscriptions = subscriptionService.findForTrueUp(id);
//        customerSubscriptions.forEach(cs -> {
//            cs.setUserAccount(null);
//            cs.setCustomerSubscriptionMappings(null);
//            cs.setBillingHeads(null);
//        });
        return subscriptionService.findForTrueUp(id);
    }

    @GetMapping("/customerSubscription/findForTrueUpCustomerSubscriptionObject/{id}")
    public List<CustomerSubscription> findForTrueUpCustomerSubscriptionObject(@PathVariable Long id) {
        List<CustomerSubscription> customerSubscriptions =
                subscriptionService.findForTrueUpCustomerSubscriptionObject(id);
        customerSubscriptions.forEach(cs -> {
            cs.setUserAccount(null);
            cs.setCustomerSubscriptionMappings(null);
            cs.setBillingHeads(null);
        });
        return customerSubscriptions;
    }

    @GetMapping("/customerSubscription")
    public List<CustomerSubscriptionDTO> findAllCustomerSubscriptions() {
        return toCustomerSubscriptionDTOs(subscriptionService.findAllCustomerSubscriptions());
    }

    @GetMapping("/customerSubscription/mark/{id}")
    public ObjectNode markForDeletion(@PathVariable Long id) {
        String message = subscriptionService.markForDeletion(id);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", message);
        return messageJson;
    }

    @GetMapping("/customerSubscription/delete/{id}")
    public ObjectNode deletionSubscription(@PathVariable Long id) {
        String message = subscriptionService.deleteSubscription(id);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", message);
        return messageJson;
    }

    @PostMapping("/customerSubscription/markedDeleted/searchBy")
    public Object searchMarkedForDeletion(@RequestBody SearchParams searchParams) {
        List<CustomerSubscription> subscriptions = subscriptionService.searchMarkedForDeletion(searchParams);
        if (!subscriptions.isEmpty()) {
            return toCustomerSubscriptionDTOs(subscriptions);
        }
        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", "No records found");
        return response;
    }

    @DeleteMapping("/customerSubscription/{id}")
    public ResponseEntity deleteCustomerSubscription(@PathVariable Long id) {
        subscriptionService.deleteCustomerSubscription(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/customerSubscription")
    public ResponseEntity deleteCustomerSubscriptions() {
        subscriptionService.deleteAllCustomerSubscriptions();
        return new ResponseEntity(HttpStatus.OK);
    }

    // SubscriptionRatesDerived /////////////////////////////////////////////
    @PostMapping("/subscriptionRatesDerived")
    public List<SubscriptionRatesDerivedDTO> addSubscriptionRatesDerived(@RequestBody List<SubscriptionRatesDerivedDTO> subscriptionRatesDerivedDTOs) {
        return toSubscriptionRatesDerivedDTOs(subscriptionService.addSubscriptionRatesDerived(toSubscriptionRatesDerived(subscriptionRatesDerivedDTOs)));
    }

    @GetMapping("/subscriptionRatesDerived")
    public List<SubscriptionRatesDerivedDTO> findAllSubscriptionRatesDerived() {
        return toSubscriptionRatesDerivedDTOs(subscriptionService.getAllSubscriptionRatesDerived());
    }

    @PostMapping("/searchBy")
    public Object search(@RequestBody SearchParams searchParams) {
        List<CustomerSubscriptionsListView> customerSubscriptionsListViewList =
                subscriptionService.comprehensiveSearch(searchParams);
        if (customerSubscriptionsListViewList != null) {
            return customerSubscriptionsListViewList;
        }
        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", "No records found");
        return response;
    }

    @GetMapping("/CustomerSubscriptionsList")
    public List<CustomerSubscriptionsListView> getAll() {
        return subscriptionService.getAll();
    }

    @PostMapping("/updateTerminationDate")
    public ObjectNode updateTerminationDate(@RequestParam Map<String, String> terminationParam) {

        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        String response = subscriptionService.updateTerminationDate(terminationParam);
        return messageJson.put("message", response);
    }

    @GetMapping("/customerInverterSubscriptionList")
    public List<UserDTO> customerInverterSubscriptionList() {
        return subscriptionService.findCustomerInverterSubscriptions();
    }

    //This api used for weather widget
    @GetMapping("/subscriptionLatLong")
    public CustomerSubscriptionRateCodeDTO subscriptionLatLongRateCodeValues(@RequestParam Long id) {
        CustomerSubscriptionRateCodeDTO customerSubscriptionCustomDTO = subscriptionService.getSubscriptionMappingLatLonRateCodes(id);
        return customerSubscriptionCustomDTO;
    }

    //this api used for maintenance
    @GetMapping("/subscriptionMaintenance")
    public CustomerSubscriptionMaintenanceDTO subscriptionMaintenanceRateCodeValues(@RequestParam Long id) {
        CustomerSubscriptionMaintenanceDTO customerSubscriptionMaintenanceDTO = subscriptionService.getSubscriptionMappingMaintenanceRateCodes(id);
        return customerSubscriptionMaintenanceDTO;
    }

    //  Contracts
    @GetMapping("/getPrivilegedCustomerSubscriptions")
    public List<CustomerSubscriptionDTO> getPrivilegedCustomerSubscriptions() {
        return toCustomerSubscriptionDTOs(subscriptionService.getPrivilegedCustomerSubscriptions());
    }

    @GetMapping("/customerInverterVariantSubscriptions")
    public List<VariantDTO> customerInverterVariantSubscriptions() {
        return subscriptionService.customerInverterVariants();
    }
    //for customers drop on power monitoring dashboard
    @PostMapping("/customerVariantSubscriptions")
    public List<InverterSubscriptionDTO> customerVariantSubscriptions(@RequestParam(value = "variantIds", required = true) List<String> variantIds) {
        return subscriptionService.getCustomerVariants(variantIds);
    }

    //for subs drop on power monitoring dashboard
    @PostMapping("/inverter/customerInverterSubsByVariantIds")
    public ResponseEntity<?> customerInverterSubsByVariantIds(@RequestParam(value = "variantIds", required = true) List<String> variantIds){
        return ResponseEntity.ok(subscriptionService.customerInverterSubsByVariantIds(variantIds));
    }

    @GetMapping("/customerInverterVariantSubscriptions/v2")
    public List<VariantDTO> customerInverterVariantSubscriptionsV2() {
        return subscriptionService.customerInverterVariantsV2();
    }

    @GetMapping("/countByCustomer")
    public List<SubscriptionCountDTO> countByCustomer() {
        return subscriptionService.countByCustomer();
    }

    @GetMapping("/getSubscriptionsByUserId/{userId}")
    public Map getSubscriptionsByUserId(@PathVariable Long userId) {
        return subscriptionService.getSubscriptionsByUserId(userId);
    }


}
