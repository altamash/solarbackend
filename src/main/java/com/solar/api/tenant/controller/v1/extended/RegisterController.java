package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.register.RegisterDetailDTO;
import com.solar.api.tenant.mapper.extended.register.RegisterHeadDTO;
import com.solar.api.tenant.mapper.extended.register.RegisterHierarchyDTO;
import com.solar.api.tenant.mapper.extended.register.RegisterMapper;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import com.solar.api.tenant.repository.RegisterHierarchyRepository;
import com.solar.api.tenant.service.extended.register.RegisterDetailService;
import com.solar.api.tenant.service.extended.register.RegisterHeadService;
import com.solar.api.tenant.service.extended.register.RegisterHierarchyService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.register.RegisterMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("RegisterController")
@RequestMapping(value = "/register")
public class RegisterController {

    @Autowired
    private RegisterHierarchyService registerHierarchyService;
    @Autowired
    private RegisterHeadService registerHeadService;
    @Autowired
    private RegisterDetailService registerDetailService;
    @Autowired
    private RegisterHierarchyRepository registerHierarchyRepository;

    // RegisterHierarchy ////////////////////////////////////////
    @PostMapping("/hierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'Write')")
    public RegisterHierarchyDTO addRegisterHierarchy(@RequestBody RegisterHierarchyDTO registerHierarchyDTO) {
        registerHierarchyDTO =
                toRegisterHierarchyDTO(registerHierarchyService.save(toRegisterHierarchy(registerHierarchyDTO)));
//        registerHierarchyService.addInMemoryRegisterHierarchies();
        return registerHierarchyDTO;
    }

    @PutMapping("/hierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'Update')")
    public RegisterHierarchyDTO updateRegisterHierarchy(@RequestBody RegisterHierarchyDTO registerHierarchyDTO) {
        RegisterHierarchy registerHierarchy =
                registerHierarchyRepository.findById(registerHierarchyDTO.getId()).orElse(null);
        registerHierarchyDTO = toRegisterHierarchyDTO(registerHierarchy == null ? registerHierarchy :
                registerHierarchyService.update(toUpdatedRegisterHierarchy(registerHierarchy,
                        toRegisterHierarchy(registerHierarchyDTO))));
//        registerHierarchyService.addInMemoryRegisterHierarchies();
        return registerHierarchyDTO;
    }

    @GetMapping("/hierarchy/{id}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public RegisterHierarchyDTO findRegisterHierarchyById(@PathVariable Long id) {
        return toRegisterHierarchyDTO(registerHierarchyService.findById(id));
    }

    @GetMapping("/hierarchy/level/{level}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> findByLevel(@PathVariable Integer level) {
        return toRegisterHierarchyDTOs(registerHierarchyService.findByLevel(level));
    }

    @GetMapping("/hierarchy/name/{name}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> findByName(@PathVariable String name) {
        return toRegisterHierarchyDTOs(registerHierarchyService.findByName(name));
    }

    @GetMapping("/hierarchy/code/{code}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'Read')")
    public RegisterHierarchyDTO findByCode(@PathVariable String code) {
        return toRegisterHierarchyDTO(registerHierarchyService.findByCode(code));
    }

    @GetMapping("/hierarchy/parent/{parent}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> findByParent(@PathVariable String parent) {
        return toRegisterHierarchyDTOs(registerHierarchyService.findByParent(parent));
    }

    @GetMapping("/hierarchy/sublevel/{level}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> findSubLevelsByLevel(@PathVariable Integer level) {
        return toRegisterHierarchyDTOs(registerHierarchyService.findSubLevelsByLevel(level));
    }

    @GetMapping("/hierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> findAllRegisterHierarchies() {
        return toRegisterHierarchyDTOs(registerHierarchyService.findAll());
    }

    @GetMapping("/addInMemoryHierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public void addInMemoryRegisterHierarchies() throws JSONException {
        registerHierarchyService.addInMemoryRegisterHierarchies();
    }

    @GetMapping("/inMemoryHierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public List<RegisterHierarchyDTO> getInMemoryRegisterHierarchies() throws JSONException {
        return RegisterMapper.toRegisterHierarchyDTOs(registerHierarchyService
                .getInMemoryRegisterHierarchies());
    }

    @GetMapping("/inMemoryHierarchy/id/{id}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'ReadAll')")
    public RegisterHierarchyDTO getInMemoryRegisterHierarchyById(@PathVariable Long id) throws JSONException {
        return RegisterMapper.toRegisterHierarchyDTO(registerHierarchyService.getInMemoryRegisterHierarchyById(id));
    }

    /*@GetMapping("/inMemoryHierarchy/{registerId}")
    public RegisterHierarchies getInMemoryRegisterHierarchiesByRegisterId(@PathVariable Long registerId) throws
    JSONException {
        return registerHierarchyService.getInMemoryRegisterHierarchies(registerId);
    }*/

    @DeleteMapping("/hierarchy/{id}")
//    @PreAuthorize("hasPermission('Hierarchy API', 'Delete')")
    public ResponseEntity deleteRegisterHierarchy(@PathVariable Long id) {
        registerHierarchyService.delete(id);
//        registerHierarchyService.addInMemoryRegisterHierarchies();
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/hierarchy")
//    @PreAuthorize("hasPermission('Hierarchy API', 'Delete')")
    public ResponseEntity deleteAllRegisterHierarchies() {
        registerHierarchyService.deleteAll();
//        registerHierarchyService.addInMemoryRegisterHierarchies();
        return new ResponseEntity(HttpStatus.OK);
    }

    // RegisterHead ////////////////////////////////////////
    @PostMapping("/head")
//    @PreAuthorize("hasPermission('Register API', 'Write')")
    public RegisterHeadDTO addRegisterHeadAndDetails(@RequestBody RegisterHeadDTO registerHeadDTO) {
        return toRegisterHeadDTO(registerHeadService.save(toRegisterHead(registerHeadDTO)));
    }

    @PutMapping("/head")
//    @PreAuthorize("hasPermission('Register API', 'Update')")
    public RegisterHeadDTO updateRegisterHeadAndDetails(@RequestBody RegisterHeadDTO registerHeadDTO) {
        RegisterHead registerHead = registerHeadService.update(toRegisterHead(registerHeadDTO));
        RegisterHeadDTO registerHeadDTOUpd = toRegisterHeadDTO(registerHead);
        registerHeadDTOUpd.setRegisterDetails(toRegisterDetailDTOs(registerDetailService.update(toRegisterDetails(registerHeadDTO.getRegisterDetails()), registerHead)));
        return registerHeadDTOUpd;
    }

    @GetMapping("/head/{id}")
//    @PreAuthorize("hasPermission('Register API', 'Read')")
    public RegisterHeadDTO findRegisterHeadById(@PathVariable Long id) {
        return toRegisterHeadDTO(registerHeadService.findById(id));
    }

    @GetMapping("/head/regModuleId/{regModuleId}")
//    @PreAuthorize("hasPermission('Register API', 'ReadAll')")
    public List<RegisterHeadDTO> findAllRegisterHeadsByRegModuleId(@PathVariable Long regModuleId) {
        return toRegisterHeadDTOs(registerHeadService.findAllByRegModuleId(regModuleId));
    }

    @GetMapping("/head")
//    @PreAuthorize("hasPermission('Register API', 'ReadAll')")
    public List<RegisterHeadDTO> findAllRegisterHeads() {
        return toRegisterHeadDTOs(registerHeadService.findAll());
    }

    // aka SubscriptionRateMatrixHead
    @GetMapping("/head/getMeasure/{registerHeadId}")
//    @PreAuthorize("hasPermission('Register API', 'Read')")
    RegisterHeadDTO findMeasureByRegisterId(@PathVariable Long registerHeadId) {
        return toRegisterHeadDTO(registerHeadService.findMeasureByRegisterId(registerHeadId));
    }

    /*@GetMapping("/head/code/{registerCode}/status/{status}")
    public List<RegisterHeadDTO> findByRegisterCodeAndStatus(@PathVariable String registerCode, @PathVariable String
    status) {
        return toRegisterHeadDTOs(registerHeadService.findByRegisterCodeAndStatus(registerCode, status));
    }*/

    @GetMapping("/head/ids/{idsCSV}")
//    @PreAuthorize("hasPermission('Register API', 'ReadAll')")
    List<RegisterHeadDTO> findByRegisterIdsIn(@PathVariable String idsCSV) {
        List<Long> ids =
                Arrays.stream(idsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        return toRegisterHeadDTOs(registerHeadService.findByRegisterIdsIn(ids));
    }
    // aka SubscriptionRateMatrixHead

   /* @GetMapping("/head/code/{registerCode}")
    public List<RegisterHeadDTO> findByRegisterCode(@PathVariable String registerCode) {
        return toRegisterHeadDTOs(registerHeadService.findByRegisterCode(registerCode));
    }*/

    @DeleteMapping("/head/{id}")
//    @PreAuthorize("hasPermission('Register API', 'Delete')")
    public ResponseEntity deleteRegisterHead(@PathVariable Long id) {
        registerHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
//    @PreAuthorize("hasPermission('Register API', 'Delete')")
    public ResponseEntity deleteAllRegisterHeads() {
        registerHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // RegisterDetail ////////////////////////////////////////
    @PostMapping("/detail")
//    @PreAuthorize("hasPermission('Register API', 'Write')")
    public RegisterDetailDTO addRegisterDetail(@RequestBody RegisterDetailDTO registerDetailDTO) {
        return toRegisterDetailDTO(registerDetailService.save(toRegisterDetail(registerDetailDTO)));
    }

    // Saving All RegisterDetail ////////////////////////////////////////
    @PostMapping("/details")
//    @PreAuthorize("hasPermission('Register API', 'Write')")
    public List<RegisterDetailDTO> addRegisterDetails(@RequestBody List<RegisterDetailDTO> registerDetailDTO) {
        return toRegisterDetailDTOs(registerDetailService.saveAll(toRegisterDetails(registerDetailDTO)));
    }

    /*@PutMapping("/details")
    public List<RegisterDetailDTO> updateRegisterDetails(@RequestBody List<RegisterDetailDTO> registerDetailDTOs) {
       return toRegisterDetailDTOs(registerDetailService.update(toRegisterDetails(registerDetailDTOs)));
    }*/

    @GetMapping("/detail/{id}")
//    @PreAuthorize("hasPermission('Register API', 'Read')")
    public RegisterDetailDTO findRegisterDetailById(@PathVariable Long id) {
        return toRegisterDetailDTO(registerDetailService.findById(id));
    }

    @GetMapping("/detail")
//    @PreAuthorize("hasPermission('Register API', 'ReadAll')")
    public List<RegisterDetailDTO> findAllRegisterDetails() {
        return toRegisterDetailDTOs(registerDetailService.findAll());
    }

    // aka SubscriptionRateMatrixDetail
    @GetMapping("/detail/measureCode/{measureCode}")
//    @PreAuthorize("hasPermission('Register API', 'Read')")
    RegisterDetailDTO findByMeasureCode(@PathVariable String measureCode) {
        return toRegisterDetailDTO(registerDetailService.findByMeasureCode(measureCode));
    }

    @GetMapping("/detail/variableByDetail/{variableByDetail}")
//    @PreAuthorize("hasPermission('Register API', 'ReadAll')")
    List<String> findMeasureCodesByVariableByDetail(@PathVariable String variableByDetail) {
        return registerDetailService.findMeasureCodesByVariableByDetail(variableByDetail);
    }
    // aka SubscriptionRateMatrixDetail

    /*@PostMapping("/headAndDetails")
    public RegisterHeadDTO addRegisterHeadAndDetails(@RequestBody RegisterHeadDTO registerHeadDTO) {
        RegisterHeadDTO registerHeadDTODB = toRegisterHeadDTO(registerHeadService.save(toRegisterHead
        (registerHeadDTO)));
        return registerHeadDTODB;
    }*/

    @DeleteMapping("/detail/delete/{id}")
//    @PreAuthorize("hasPermission('Register API', 'Delete')")
    public ResponseEntity deleteRegisterDetail(@PathVariable Long id) {
        registerDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
//    @PreAuthorize("hasPermission('Register API', 'Delete')")
    public ResponseEntity deleteAllRegisterDetails() {
        registerDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
