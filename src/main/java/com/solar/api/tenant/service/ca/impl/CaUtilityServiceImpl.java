package com.solar.api.tenant.service.ca.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.ca.CaUtilityRepository;
import com.solar.api.tenant.service.ca.CaUtilityService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.solar.api.tenant.mapper.ca.CaUtilityMapper.toCaUtility;
import static com.solar.api.tenant.mapper.ca.CaUtilityMapper.toUpdateCaUtility;

@Service
public class CaUtilityServiceImpl implements CaUtilityService<CaUtility> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private CaUtilityRepository caUtilityRepository;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private StorageService storageService;
    @Autowired
    private Utility utility;
    @Autowired
    private EntityService entityService;

    @Override
    public CaUtility update(CaUtility obj) {
        CaUtility caUtility= caUtilityRepository.findById(obj.getId())
                .orElseThrow(() -> new NotFoundException("Utility id  not found: " + obj.getId()));
        return caUtilityRepository.save(toUpdateCaUtility(caUtility,obj));
    }

    @Override
    public String delete(Long id) {
        caUtilityRepository.deleteById(id);
        return "deleted";
    }

    @Override
    public List<CaUtility> getAll() {
        return caUtilityRepository.findAll();
    }

    @Override
    public CaUtility getById(Long id) {
        return caUtilityRepository.findById(id).orElseThrow(() -> new RuntimeException(" Utility id  not found: " + id));
    }

    @Override
    public List<CaUtility> getByEntity(Entity entity) {
        return caUtilityRepository.findByEntity(entity);
    }

    @Override
    public CaUtility save(CaUtilityDTO caUtilityDTO, User user, List<MultipartFile> multipartFiles) {
        caUtilityDTO.setCreatedAt(LocalDateTime.now());
        Entity entity = entityService.findEntityByUserId(user.getAcctId());
        if(entity == null) throw new SolarApiException("can not find entity with entity id="+caUtilityDTO.getEntityId());
        CaUtility caUtility = toCaUtility(caUtilityDTO);
        caUtility.setEntity(entity);
        caUtilityRepository.save(caUtility);
        doAttachmentToUser(multipartFiles,user,caUtility, Constants.CUSTOMER_ACQ.CA_UTILITY);
        return caUtility;
    }

    @Override
    public List<CaUtility> saveAll(List<CaUtility> caUtilities) {
        return caUtilityRepository.saveAll(caUtilities);
    }

    @Override
    public CaUtility save(CaUtilityDTO caUtilityDTO) {
        return caUtilityRepository.save(toCaUtility(caUtilityDTO));
    }

    private List<DocuLibrary> doAttachmentToUser(List<MultipartFile> multipartFiles, User user, CaUtility cautility, String businessInfoPath) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        DocuLibrary docuLibrary = null;
        String directoryPath = "tenant/" + utility.getCompKey() + Constants.CUSTOMER_ACQ.CA_UTILITY + user.getAcctId() + "/"+businessInfoPath;
        try {
            if(multipartFiles != null && !multipartFiles.isEmpty()) {
                for (MultipartFile multipartFile : multipartFiles) {
                    if (multipartFile != null && !multipartFile.isEmpty()) {
                        //if not profile then take original name
                        String originalFileName = multipartFile.getOriginalFilename();
                        String uri = storageService.storeInContainer(multipartFile, appProfile, directoryPath,
                                timeStamp + "-" + originalFileName, utility.getCompKey(), false);
                        docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                                .docuName(multipartFile.getOriginalFilename())
                                .uri(uri)
                                .docuType(multipartFile.getContentType())
                                .visibilityKey(true)
                                .codeRefType(Constants.CUSTOMER_ACQ.CA_UTILITY)
                                .codeRefId(String.valueOf(cautility.getId()))
                                .referenceTime(timeStamp)
                                .build());
                        if (docuLibrary.getDocuId() != null)
                            docuLibraryList.add(docuLibrary);
                    }
                }
            }
        } catch (URISyntaxException | StorageException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return docuLibraryList;
    }

    @Override
    public ObjectNode markUtilityAsPrimary(Long entityId, Long utilityId) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        Entity entity = entityService.findById(entityId);
        if(entity != null) {
            List<CaUtility> caUtilities = caUtilityRepository.findByEntity(entity);
            caUtilities.stream().forEach(caUtility -> {
                if (caUtility.getId().longValue() == utilityId.longValue()) {
                    caUtility.setIsPrimary(true);
                } else {
                    caUtility.setIsPrimary(false);
                }
            });
            caUtilityRepository.saveAll(caUtilities);
            return response.put("message", "utility marked as primary.");
        }else{
            return response.put("message", "Entity not found.");
        }
    }

}
