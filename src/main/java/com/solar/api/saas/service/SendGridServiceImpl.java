package com.solar.api.saas.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.mapper.sendgridDTO.TemplateDTO;
import com.solar.api.saas.mapper.sendgridDTO.VersionDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.model.workflow.MessageTemplate;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.repository.workflow.MessageTemplateRepository;
import com.solar.api.tenant.service.preferences.EConfigParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.solar.api.tenant.mapper.workflows.MessageTemplateMapper.toUpdatedMessageTemplate;

@Service
public class SendGridServiceImpl implements SendGridService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    EmailService emailService;

    private final String BASE_URL = "https://api.sendgrid.com/v3";
    private final String TEMPLATES = "/templates";
    private final String TEMPLATE_ID = "/templates/{templatesId}";
    private final String VERSIONS = "/versions";
    private final String VERSION_ID = "/versions/{versionId}";

    @Override
    public VersionDTO addTemplateAndVersion(TemplateDTO templateDTO) {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        headers.put("Authorization", Arrays.asList("Bearer " + emailService.getSendGridApiKey()));
        ResponseEntity<TemplateDTO> templateResponse = WebUtils.submitRequest(HttpMethod.POST, BASE_URL + TEMPLATES,
                templateDTO, headers, TemplateDTO.class);
        MessageTemplate messageTemplate = findByMsgTmplName(templateDTO.getName());
        messageTemplate.setParentTmplId(templateResponse.getBody().getId());
        try {
            addOrUpdate(messageTemplate);
        } catch (AlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
        }
        VersionDTO versionRequest = VersionDTO.builder()
                .template_id(templateResponse.getBody().getId())
                .name(messageTemplate.getMsgTmplName())
                .html_content(getFinalTemplate(messageTemplate))
                .generate_plain_content(true)
                .plain_content(" ")
                .editor("code")
                .build();
        ResponseEntity<VersionDTO> versionResponse = WebUtils.submitRequest(HttpMethod.POST, BASE_URL + TEMPLATES + "/" +
                        templateResponse.getBody().getId() + VERSIONS, versionRequest, headers, VersionDTO.class);

        return versionResponse.getBody();
    }

    private String getFinalTemplate(MessageTemplate messageTemplate) {
        StringBuilder finalTemplate = new StringBuilder();
        if (messageTemplate.getHeader() != null) {
            finalTemplate.append(messageTemplate.getHeader());
        } else {

            Optional<TenantConfig> header = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_HEADER.getName());
            header.ifPresent(h -> {
                if (h.getText() != null) {
                    finalTemplate.append(h.getText());
                }
            });
        }
        finalTemplate.append(messageTemplate.getTemplateHTMLCode());
        if (messageTemplate.getFooter() != null) {
            finalTemplate.append(messageTemplate.getFooter());
        } else {
            Optional<TenantConfig> footer = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_FOOTER.getName());
            footer.ifPresent(f -> {
                if (f.getText() != null) {
                    finalTemplate.append(f.getText());
                }
            });
        }
        return finalTemplate.toString();
    }

    @Override
    public TemplateDTO addTemplate(TemplateDTO templateDTO) {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        headers.put("Authorization", Arrays.asList("Bearer " + emailService.getSendGridApiKey()));

        ResponseEntity<TemplateDTO> response = WebUtils.submitRequest(HttpMethod.POST, BASE_URL + TEMPLATES,
                templateDTO, headers, TemplateDTO.class);
        return response.getBody();
    }

    @Override
    public VersionDTO addVersion(VersionDTO versionDTO) {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        headers.put("Authorization", Arrays.asList("Bearer " + emailService.getSendGridApiKey()));

        ResponseEntity<VersionDTO> response = WebUtils.submitRequest(HttpMethod.POST, BASE_URL + TEMPLATE_ID + VERSIONS,
                versionDTO, headers, VersionDTO.class, versionDTO.getTemplate_id());
        return response.getBody();
    }

    public MessageTemplate addOrUpdate(MessageTemplate messageTemplate) throws AlreadyExistsException {
        if (messageTemplate.getId() != null) {
            MessageTemplate messageTemplateData = findById(messageTemplate.getId());
            if (messageTemplateData == null) {
                throw new NotFoundException(SubscriptionRatesDerived.class, messageTemplate.getId());
            }
            return messageTemplateRepository.save(toUpdatedMessageTemplate(messageTemplateData,
                    messageTemplate));
        }
        return messageTemplateRepository.save(messageTemplate);
    }

    public MessageTemplate findById(Long id) {
        return messageTemplateRepository.findById(id).orElseThrow(()
                -> new NotFoundException(MessageTemplate.class, id));
    }

    public MessageTemplate findByMsgTmplName(String msgTmplName) {
        return messageTemplateRepository.findByMsgTmplName(msgTmplName).orElseThrow(()
                -> new NotFoundException(MessageTemplate.class, "msgTmplName", msgTmplName));
    }


}
