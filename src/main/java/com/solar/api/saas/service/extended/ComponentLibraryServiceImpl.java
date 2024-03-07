package com.solar.api.saas.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.permission.component.ComponentLibraryMapper;
import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.component.ComponentTypeProvision;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.repository.ComponentTypeProvisionRepository;
import com.solar.api.saas.repository.permission.ComponentLibraryRepository;
import com.solar.api.saas.service.process.permission.PermissionService;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.tenant.controller.v1.extended.OrderController;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Service
public class ComponentLibraryServiceImpl implements ComponentLibraryService {

    private static String[] PERMISSIONS = {"Read", "ReadAll", "Write", "Update", "Delete"};
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ComponentLibraryRepository repository;
    @Autowired
    private ComponentTypeProvisionRepository componentTypeProvisionRepository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PermissionSetService permissionSetService;
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public Map<String, List<String>> addComponents(Long provisionId, String option) {
//        RequestMappingHandlerMapping requestMappingHandlerMapping = context
//                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();
        map.forEach((key, value) -> LOGGER.info("{} {}", key, value));
        System.out.println("Endpoints--------------->>");

        Optional<ComponentTypeProvision> apiProvisionOptional = componentTypeProvisionRepository.findById(provisionId);
        ComponentTypeProvision provision = apiProvisionOptional.get();

//        List<ComponentLibrary> library = componentLibraryService.findByCompReference("API");
        Map<String, List<String>> controllerMethodsMap = new TreeMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo key = entry.getKey();
            HandlerMethod value = entry.getValue();
            if (value.getBeanType() == BasicErrorController.class) {
                continue;
            }
            if (controllerMethodsMap.get(value.getBean()) == null) {
                controllerMethodsMap.put(value.getBean().toString(), new ArrayList<>());
            }
//            controllerMethodsMap.get(value.getBeanType()).add(key.toString());
            Set<String> patternsCondition = key.getPatternsCondition().getPatterns();
            Set<RequestMethod> methodsCondition = key.getMethodsCondition().getMethods();
//            controllerMethodsMap.get(value.getBean()).add(methodsCondition.stream().findFirst().get().name() + "_" + patternsCondition.stream().findFirst().get());
            controllerMethodsMap.get(value.getBean())
//                    .add(value.getBean() + "_" + (WordUtils.capitalize(value.getMethod().getName() + WordUtils.capitalize(methodsCondition.stream().findFirst().get().name())).replaceAll("([^_A-Z])([A-Z])", "$1_$2")));
//                    .add(value.getBean() + "_" + ((methodsCondition.stream().findFirst().get().name() + "_" + WordUtils.capitalize(value.getMethod().getName())).replaceAll("([^_A-Z])([A-Z])", "$1_$2")));
                    .add(methodsCondition.stream().findFirst().get().name() + "_" + patternsCondition.stream().findFirst().get()
+"#"+
                            value.getBean() + "_" + ((methodsCondition.stream().findFirst().get().name() + "_" + WordUtils.capitalize(value.getMethod().getName())).replaceAll("([^_A-Z])([A-Z])", "$1_$2"))
                        );
//            ((methodsCondition.stream().findFirst().get().name() + " " + WordUtils.capitalize(value.getMethod().getName())).replaceAll("([^_A-Z])([A-Z])", "$1_$2"))
            if (value.getBeanType() == OrderController.class) {
                Class clazz = value.getBeanType();
            }


            int patternsConditionSize = patternsCondition.size();
            if (patternsConditionSize > 1) {
                LOGGER.info(String.valueOf(patternsConditionSize));
            }

            int methodsConditionSize = methodsCondition.size();
            if (methodsConditionSize > 1) {
                LOGGER.info(String.valueOf(methodsConditionSize));
            }
            methodsCondition.forEach(m -> {
                LOGGER.info(m.name());
            });
        }
        if ("1".equals(option)) {
            long autoIncrement = getNextIdentifier(provision.getCompReference());
//            Long autoIncrementPermissions = null;
            Long autoIncrementPermissions = permissionService.getNextIdentifier(provision.getCompReference());
//            List<ComponentLibrary> componentLibraries = new ArrayList<>();
            List<Permission> permissions = new ArrayList<>();
            List<String> apiComponents = new ArrayList(controllerMethodsMap.keySet());
            Collections.sort(apiComponents);
            for (Map.Entry<String, List<String>> entry : controllerMethodsMap.entrySet()) {
                Collections.sort(entry.getValue());
                String componentName = entry.getKey() + "_" + provision.getCompReference();
                ComponentLibrary library = repository.findByComponentName(componentName);
                if (library == null) {
                    library = saveOrUpdate(ComponentLibrary.builder()
                            .id(autoIncrement++)
                            .componentName(componentName)
                            .description(entry.getKey() + " APIs")
                            .componentTypeProvision(provision)
                            .build(), provision.getId());
                }
//                if (autoIncrementPermissions == null) {
//                Long autoIncrementPermissions = permissionService.getNextIdentifier(provisionId);
//                }
                for (String permission : entry.getValue()) {
                    String[] parts = permission.split("#");
                    String[] descParts = parts[1].split("_");
                    Permission p = permissionService.findByName(parts[0]);
                    if (p == null) {
                        permissions.add(Permission.builder()
                                .id(autoIncrementPermissions++)
                                .name(parts[0])
                                .description(descParts[0] + "; " + descParts[1] + "; " + WordUtils.uncapitalize(String.join("", ArrayUtils.subarray(descParts, 2, descParts.length))) + "()")
                                .componentLibrary(library)
                                .build());
                    }
                }
            }
            permissionService.saveAll(permissions);
            /*for (String key : apiComponents) {
//                ComponentLibrary libraryDb =
//                        componentLibraryService.findByComponentName(key.getSimpleName() + "_" + provision.getCompReference());
//                if (libraryDb == null) {
                ComponentLibrary library = saveOrUpdate(ComponentLibrary.builder()
                        .id(autoIncrement++)
                        .componentName(key + "_" + provision.getCompReference())
                        .description(key + " APIs")
                        .componentTypeProvision(provision)
                        .build(), provision.getId());
//                }
            }*/
            /*List<String> permissions =
                    controllerMethodsMap.values().stream().flatMap(m -> m.stream()).collect(Collectors.toList());*/
//            Collections.sort(permissions);
            LOGGER.info("");
        }
        return controllerMethodsMap;
    }

    @Override
    public List<ComponentLibrary> addComponents(Long provisionId, Long parentId, List<String> selectors) {
        List<ComponentLibrary> componentLibraries = new ArrayList<>();
        Optional<ComponentTypeProvision> apiProvisionOptional = componentTypeProvisionRepository.findById(provisionId);
        ComponentTypeProvision provision = apiProvisionOptional.get();
        if (ECompReference.UI.getType().equals(provision.getCompReference())) {
            if (parentId != null) {
                repository.findById(parentId).orElseThrow(() -> new NotFoundException(ComponentLibrary.class, parentId));
            }
            for (String selector : selectors) {
                componentLibraries.add(ComponentLibrary.builder()
                        .id(getNextIdentifier(provision.getCompReference()))
                        .componentName(WordUtils.capitalize(selector.replaceAll("-", " ")).replaceAll(" ", "_") + "_UI")
                        .description(WordUtils.capitalize(selector.replaceAll("app-", "").replaceAll("-", " ")))
                        .componentTypeProvision(provision)
                        .parentId(parentId)
                        .build());
            }
        }
        componentLibraries = repository.saveAll(componentLibraries);
        addComponentPermissions(provisionId, componentLibraries);
        return componentLibraries;
    }

    @Transactional
    private void addComponentPermissions(Long provisionId, List<ComponentLibrary> componentLibraries) {
        List<PermissionSet> permissionSets = new ArrayList<>();
        List<Permission> permissions = new ArrayList<>();
        Optional<ComponentTypeProvision> apiProvisionOptional = componentTypeProvisionRepository.findById(provisionId);
        ComponentTypeProvision provision = apiProvisionOptional.get();
        if (ECompReference.UI.getType().equals(provision.getCompReference())) {
            for (ComponentLibrary componentLibrary : componentLibraries) {
                permissionSets.addAll(addPermissionSets(componentLibrary, provision.getCompReference()));
                permissions.addAll(addPermissions(componentLibrary, provision.getCompReference()));
            }
        }
        permissionSets = permissionSetService.saveAll(permissionSets);
        permissions = permissionService.saveAll(permissions);

        for (int i = 0; i < permissionSets.size(); i++) {

        }
    }

    private List<PermissionSet> addPermissionSets(ComponentLibrary componentLibrary, String compReference) {
        List<PermissionSet> permissionSets = new ArrayList<>();
        if (ECompReference.UI.getType().equals(compReference)) {
            Arrays.asList(PERMISSIONS).forEach(permission -> {
                String componentName = componentLibrary.getComponentName();
                if (componentName.endsWith("_UI")) {
                    componentName = componentName.substring(0, componentName.length() - ECompReference.UI.getType().length());
                }
                String componentDescription = componentName;
                if (componentName.startsWith("App_")) {
                    componentDescription = componentName.substring(4);
                }
                permissionSets.add(PermissionSet.builder()
                        .id(permissionSetService.getNextIdentifier(compReference))
                        .name(componentName + "_" + permission)
                        .description(componentDescription.replaceAll("-", " ") + " " + permission)
                        .build());
            });
        }
        return permissionSets;
    }

    private List<Permission> addPermissions(ComponentLibrary componentLibrary, String compReference) {
        List<Permission> permissions = new ArrayList<>();
        if (ECompReference.UI.getType().equals(compReference)) {
            Arrays.asList(PERMISSIONS).forEach(permission -> {
                String componentName = componentLibrary.getComponentName();
                String componentDescription = componentName;
                if (componentName.startsWith("App_")) {
                    componentDescription = componentName.substring(4);
                }
                permissions.add(Permission.builder()
                        .id(permissionService.getNextIdentifier(compReference))
                        .name(componentName + "_" + permission)
                        .description(componentDescription.replaceAll("-", " ") + " " + permission)
                        .componentLibrary(componentLibrary)
                        .build());
            });
        }
        return permissions;
    }

    @Override
    public ComponentLibrary saveOrUpdate(ComponentLibrary componentLibrary, Long componentTypeProvisionId) {
        if (componentTypeProvisionId != null) {
            componentTypeProvisionRepository.findById(componentTypeProvisionId).ifPresent(p -> componentLibrary.setComponentTypeProvision(p));
        }
        ComponentLibrary componentLibraryData = repository.findById(componentLibrary.getId()).orElse(null);
        if (componentLibraryData != null) {
            ComponentLibrary isExist =
                    repository.findByComponentName(componentLibrary.getComponentName());
            if (isExist != null && isExist.getId().longValue() != componentLibrary.getId().longValue()) {
//                throw new AlreadyExistsException(componentLibrary.getComponentName());
                LOGGER.warn("Already exists");
                return null;
            }
            componentLibraryData = ComponentLibraryMapper.toUpdatedComponentLibrary(componentLibraryData,
                    componentLibrary);
            return repository.save(componentLibraryData);
        }
        ComponentLibrary isExist = repository.findByComponentName(componentLibrary.getComponentName());
        if (isExist != null) {
//            throw new AlreadyExistsException(componentLibrary.getComponentName());
            LOGGER.warn("Already exists");
            return null;
        }
        return repository.save(componentLibrary);
    }

    @Override
    public ComponentLibrary findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ComponentLibrary.class, id));
    }

    @Override
    public List<ComponentLibrary> findByLevel(Integer level) {
        return repository.findByLevel(level);
    }

    @Override
    public ComponentLibrary findByComponentName(String componentName) {
        return repository.findByComponentName(componentName);
    }

    @Override
    public List<ComponentLibrary> findByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Override
    public List<ComponentLibrary> findByComponentTypeProvision(Long componentTypeProvisionId) {
        ComponentTypeProvision typeProvision =
                componentTypeProvisionRepository.findById(componentTypeProvisionId).orElseThrow(() -> new NotFoundException(ComponentTypeProvision.class, componentTypeProvisionId));
        return repository.findByComponentTypeProvision(typeProvision);
    }

    @Override
    public List<ComponentLibrary> findSubLevelsByLevel(Integer level) {
        return repository.findByLevel(++level);
    }

    @Override
    public List<ComponentLibrary> findAll() {
        return repository.findAll();
    }

    @Override
    public Long getNextIdentifier(String compReference) {
        Long lastIdentifier = null;
        if (ECompReference.UI.getType().equals(compReference)) {
            lastIdentifier = repository.getLastIdentifier(50000l);
        } else if (ECompReference.API.getType().equals(compReference)) {
            lastIdentifier = repository.getLastIdentifier(100000l);
        }
        return lastIdentifier == null ? 1 : lastIdentifier + 1;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    public static void main(String[] a) {
        String s = "App_Terms_Condition_UI";
        System.out.println("App_Terms_Condition_UI");
    }

}
