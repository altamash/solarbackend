package com.solar.api.saas.controller.v1.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.mapper.permission.component.ComponentLibraryDTO;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.repository.permission.ComponentLibraryRepository;
import com.solar.api.saas.service.extended.ComponentLibraryService;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solar.api.saas.mapper.permission.component.ComponentLibraryMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASComponentLibraryController")
@RequestMapping(value = "/saas/componentLibrary")
public class SAASComponentLibraryController implements BeanNameAware {

    private String beanName = "CompLibraryController";

    @Autowired
    private ComponentLibraryService componentLibraryService;
    @Autowired
    private ComponentLibraryRepository componentLibraryRepository;

    @GetMapping("/addComponents/provision/{provisionId}/{option}")
    public Map<String, List<String>> updateComponents(@PathVariable Long provisionId, @PathVariable String option) {
        return componentLibraryService.addComponents(provisionId, option);
    }

    @GetMapping("/add/provision/{provisionId}/componentCSV/{componentCSV}")
    public List<ComponentLibraryDTO> addComponents(@RequestParam(required = false) Long parentId,
                                                   @PathVariable Long provisionId, @PathVariable String componentCSV) {
        return toComponentLibraryDTOs(componentLibraryService.addComponents(2l, parentId,
                Arrays.asList(componentCSV.split(",")).stream().map(c -> c.trim()).collect(Collectors.toList())));
    }

    @PostMapping
    public ComponentLibraryDTO add(@RequestBody ComponentLibraryDTO componentLibraryDTO, @RequestParam(value =
            "componentTypeProvisionId", required = false) Long componentTypeProvisionId) {
        return toComponentLibraryDTO(componentLibraryService.saveOrUpdate(toComponentLibrary(componentLibraryDTO),
                componentTypeProvisionId));
    }

    @PutMapping
    public ComponentLibraryDTO update(@RequestBody ComponentLibraryDTO componentLibraryDTO, @RequestParam(value =
            "componentTypeProvisionId", required = false) Long componentTypeProvisionId) {
        ComponentLibrary componentLibrary =
                componentLibraryRepository.findById(componentLibraryDTO.getId()).orElse(null);
        return toComponentLibraryDTO(componentLibrary == null ? componentLibrary :
                componentLibraryService.saveOrUpdate(toUpdatedComponentLibrary(componentLibrary,
                        toComponentLibrary(componentLibraryDTO)), componentTypeProvisionId));
    }

    @GetMapping("/{id}")
    public ComponentLibraryDTO findById(@PathVariable Long id) {
        return toComponentLibraryDTO(componentLibraryService.findById(id));
    }

    @GetMapping("/level/{level}")
    public List<ComponentLibraryDTO> findByLevel(@PathVariable Integer level) {
        return toComponentLibraryDTOs(componentLibraryService.findByLevel(level));
    }

    @GetMapping("/componentName/{componentName}")
    public ComponentLibraryDTO findByComponentName(@PathVariable String componentName) {
        return toComponentLibraryDTO(componentLibraryService.findByComponentName(componentName));
    }

    @GetMapping("/parentId/{parentId}")
    public List<ComponentLibraryDTO> findByParentId(@PathVariable Long parentId) {
        return toComponentLibraryDTOs(componentLibraryService.findByParentId(parentId));
    }

    @GetMapping("/provisionId/{componentTypeProvisionId}")
    public List<ComponentLibrary> findByComponentTypeProvision(Long componentTypeProvisionId) {
        return componentLibraryService.findByComponentTypeProvision(componentTypeProvisionId);
    }

    @GetMapping("/sublevel/{level}")
    public List<ComponentLibraryDTO> findSubLevelsByLevel(@PathVariable Integer level) {
        return toComponentLibraryDTOs(componentLibraryService.findSubLevelsByLevel(level));
    }

    @GetMapping
    public List<ComponentLibraryDTO> findAll() {
        return toComponentLibraryDTOs(componentLibraryService.findAll());
    }

    @DeleteMapping("/{id}")
    public ObjectNode delete(@PathVariable Long id) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        componentLibraryService.delete(id);
        response.put("message", "Deleted Successfully");
        return response;
    }

    @DeleteMapping
    public ObjectNode deleteAll() {
        ObjectNode response = new ObjectMapper().createObjectNode();
        componentLibraryService.deleteAll();
        response.put("message", "Deleted Successfully");
        return response;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String getBeanName() {
        return this.beanName;
    }
}
