package com.solar.api.tenant.service.widgetconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.Utility;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.widgetconfiguration.EndPointsDTO;
import com.solar.api.tenant.mapper.widgetconfiguration.ModuleWidgetDTO;
import com.solar.api.tenant.mapper.widgetconfiguration.UserWidgetDTO;
import com.solar.api.tenant.mapper.widgetconfiguration.WidgetConfigurationMapper;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.widgetconfiguration.ModuleWidget;
import com.solar.api.tenant.model.widgetconfiguration.UserWidget;
import com.solar.api.tenant.model.widgetconfiguration.Endpoint;
import com.solar.api.tenant.repository.RoleRepository;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.widgetconfiguration.EndpointRepository;
import com.solar.api.tenant.repository.widgetconfiguration.UserWidgetRepository;
import com.solar.api.tenant.repository.widgetconfiguration.ModuleWidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WidgetConfigurationServiceImpl implements WidgetConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetConfigurationServiceImpl.class);
    private final String sasToken;
    private final String blobService;
    private final String container;
    private final String mongoBaseUrl;
    private final MasterTenantRepository masterTenantRepository;
    private final UserWidgetRepository userWidgetRepository;
    private final ModuleWidgetRepository moduleWidgetRepository;
    private final EndpointRepository endpointRepository;
    private final ObjectMapper mapper;
    private final RoleRepository roleRepository;
    private final EntityRoleRepository entityRoleRepository;
    private final Utility utility;

    public WidgetConfigurationServiceImpl(@Value("${app.storage.azureBlobSasToken}") String sasToken,
                                          @Value("${app.storage.blobService}") String blobService,
                                          @Value("${app.storage.container}") String container,
                                          @Value("${app.mongoBaseUrl}") String mongoBaseUrl,
                                          UserWidgetRepository userWidgetRepository,
                                          MasterTenantRepository masterTenantRepository, ModuleWidgetRepository moduleWidgetRepository,
                                          EndpointRepository endpointRepository, ObjectMapper mapper, RoleRepository roleRepository, EntityRoleRepository entityRoleRepository, DataExchange dataExchange, Utility utility) {
        this.sasToken = sasToken;
        this.blobService = blobService;
        this.container = container;
        this.mongoBaseUrl = mongoBaseUrl;
        this.userWidgetRepository = userWidgetRepository;
        this.masterTenantRepository = masterTenantRepository;
        this.moduleWidgetRepository = moduleWidgetRepository;
        this.endpointRepository = endpointRepository;
        this.mapper = mapper;
        this.roleRepository = roleRepository;
        this.entityRoleRepository = entityRoleRepository;
        this.utility = utility;
    }

    @Override
    public BaseResponse getAllEndPoints() {
        try{
        List<Endpoint> endPoints = endpointRepository.findAll();
        List<EndPointsDTO> endPointsDTOList = WidgetConfigurationMapper.toEndPointList(endPoints);
        return BaseResponse.builder()
                .message("Endpoints retrieved successfully")
                .data(endPointsDTOList)
                .code(HttpStatus.OK.value())
                .build();
        } catch (Exception ex) {
            return BaseResponse.builder()
                   .message(ex.getMessage())
                   .data(null)
                   .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                   .build();
        }
    }

    @Override
    public BaseResponse getAllWidgets() {
        try{
            List<ModuleWidget> moduleWidgets = moduleWidgetRepository.findAll();
            List<ModuleWidgetDTO> moduleWidgetDTOList = WidgetConfigurationMapper.toModuleWidgetList(moduleWidgets);
            return BaseResponse.builder()
                    .message("moduleWidgets retrieved successfully")
                    .data(moduleWidgetDTOList)
                    .code(HttpStatus.OK.value())
                    .build();
        } catch (Exception ex) {
            return BaseResponse.builder()
                    .message(ex.getMessage())
                    .data(null)
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .build();
        }
    }

    @Override
    public ResponseEntity saveOrUpdateUserWidget(UserWidgetDTO userWidgetDTO, Long compKey) {
        try {
            if (userWidgetDTO == null) {
                return utility.buildErrorResponse(HttpStatus.EXPECTATION_FAILED, "userWidgetDTO cannot be null");
            }
            UserWidget userWidget = null;
            if (userWidgetDTO.getId() != null) {
                userWidget = userWidgetRepository.findById(userWidgetDTO.getId()).orElse(null);
                if (userWidget == null) {
                    return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "Cannot find userWidget with id: + userWidgetDTO.getId()");
                }
            }
            ModuleWidget moduleWidget = moduleWidgetRepository.findById(userWidgetDTO.getModuleWidgetId()).orElse(null);
            Endpoint endpoint =  endpointRepository.findById(userWidgetDTO.getEndpointId()).orElse(null);

            if (moduleWidget == null || endpoint == null) {
                return utility.buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid data provided");
            }
            if (userWidget != null) {
                // Update existing UserWidget
                userWidget.setWidgetName(userWidgetDTO.getWidgetName() != null ? userWidgetDTO.getWidgetName() : userWidget.getWidgetName());
                userWidget.setModuleWidget(moduleWidget);
                userWidget.setEndpoint(endpoint);
                userWidgetRepository.save(userWidget);
                return utility.buildSuccessResponse(HttpStatus.OK, "UserWidget updated successfully", null);
            } else {
                // Save new UserWidget
                userWidget = WidgetConfigurationMapper.toUserWidget(userWidgetDTO);
                userWidget.setModuleWidget(moduleWidget);
                userWidget.setEndpoint(endpoint);
                userWidgetRepository.save(userWidget);
                return utility.buildSuccessResponse(HttpStatus.OK, "UserWidget saved successfully with id:"+userWidget.getId(), null);
            }
        } catch (Exception ex) {
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
        }
    }

    @Override
    public BaseResponse getLoggedInUserWidgets(Long acctId, Long compKey) {
        try {
            if (acctId != null) {
                List<UserWidget> userWidgets = userWidgetRepository.findByAcctId(acctId);
                if (userWidgets != null) {
                    List<UserWidgetDTO> userWidgetDTOList = WidgetConfigurationMapper.toUserWidgetDTOList(userWidgets);
                    return BaseResponse.builder()
                            .message("userWidgets retrieved successfully")
                            .data(userWidgetDTOList)
                            .code(HttpStatus.OK.value())
                            .build();
                } else {
                    return BaseResponse.builder()
                            .message("userWidgets not found")
                            .data(null)
                            .code(HttpStatus.OK.value())
                            .build();
                }
            } else {
                return BaseResponse.builder()
                        .message("acctId cannot be null")
                        .data(null)
                        .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .build();
            }
        } catch (Exception ex) {
            return BaseResponse.builder()
                    .message(ex.getMessage())
                    .data(null)
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .build();
        }
    }
}
