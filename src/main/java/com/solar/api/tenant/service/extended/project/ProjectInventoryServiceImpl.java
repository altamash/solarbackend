package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.tenant.mapper.extended.assetHead.AssetMapper;
import com.solar.api.tenant.mapper.extended.project.PagedProjectInventorySerialDTO;
import com.solar.api.tenant.mapper.extended.project.ProjectMapper;
import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import com.solar.api.tenant.repository.project.ProjectInventoryRepository;
import com.solar.api.tenant.repository.project.ProjectInventorySerialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.toUpdatedProjectInventory;
import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.toUpdatedProjectInventorySerial;

@Service
public class ProjectInventoryServiceImpl implements ProjectInventoryService {

    @Autowired
    private ProjectInventoryRepository projectInventoryRepository;
    @Autowired
    private ProjectInventorySerialRepository projectInventorySerialRepository;
    @Autowired
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    private AssetSerialNumberRepository assetSerialNumberRepository;

    @Override
    public ProjectInventory save(ProjectInventory projectInventory) {
        ProjectInventory  projectInventoryDB = projectInventoryRepository.findByAssetIdAndProjectId(projectInventory.getAssetId(), projectInventory.getProjectId());
        if(projectInventoryDB == null ){
            projectInventoryDB = projectInventoryRepository.save(projectInventory);
        }
        //update
        List<ProjectInventorySerial> projectInventorySerials;
        if (Objects.nonNull(projectInventory.getProjectInventorySerials())) {
            projectInventorySerials = new ArrayList<>();
            ProjectInventory finalProjectInventory = projectInventoryDB;
            projectInventory.getProjectInventorySerials().forEach(serials-> {
                ProjectInventorySerial projectInventorySerial = new ProjectInventorySerial();
                projectInventorySerial.setProjectInventoryId(finalProjectInventory.getId());
                projectInventorySerial.setProjectInventory(finalProjectInventory);
                projectInventorySerial.setAssetSerialNumberId(serials.getAssetSerialNumberId());
                projectInventorySerials.add(projectInventorySerial);
            });
            if (Objects.nonNull(projectInventorySerials)) {
                projectInventorySerialRepository.saveAll(projectInventorySerials);
            }
        }
       /* if (Objects.nonNull(projectInventory.getProjectInventorySerials())) {
            projectInventory.getProjectInventorySerials().forEach(serials-> {

            });
        }*/
        return projectInventoryDB;
    }

    @Override
    public ProjectInventory findById(Long projectInventoryId) {
        return projectInventoryRepository.findById(projectInventoryId).orElseThrow(() -> new NotFoundException(ProjectInventory.class, projectInventoryId));
    }

    @Override
    public ProjectInventory update(ProjectInventory projectInventory) {
        ProjectInventory projectInventoryDB = toUpdatedProjectInventory(findById(projectInventory.getId()), projectInventory);
        ProjectInventory finalProjectInventoryDB = projectInventoryRepository.save(projectInventoryDB);
        List<ProjectInventorySerial> projectInventorySerials = new ArrayList<>();
        if (projectInventory.getProjectInventorySerials().size() != 0) {
            projectInventory.getProjectInventorySerials().forEach(serial-> {
                ProjectInventorySerial projectInventorySerialDB = projectInventorySerialRepository.findById(serial.getId()).get();
                projectInventorySerialDB.setProjectInventory(finalProjectInventoryDB);
                projectInventorySerialDB = toUpdatedProjectInventorySerial(projectInventorySerialDB, serial);
                projectInventorySerials.add(projectInventorySerialDB);
            });
            projectInventorySerialRepository.saveAll(projectInventorySerials);
            finalProjectInventoryDB.setProjectInventorySerials(projectInventorySerials);
        }
        return finalProjectInventoryDB;
    }

    @Override
    public List<ProjectInventory> findAllByProjectId(Long projectId) {
        List<ProjectInventory> projectInventories = projectInventoryRepository.findAllByProjectId(projectId);
        projectInventories.forEach(inventory-> {
            AssetHead assetHead = assetHeadRepository.findById(inventory.getAssetId()).get();
            if (Objects.nonNull(assetHead.getAssetDetails())) {
                //for model no.
                AssetDetail assetDetail = assetHead.getAssetDetails().stream().filter(measure-> measure.getMeasureCodeId() == 79 ).findFirst().orElse(null);
                if (assetDetail != null) {
                    inventory.setModelNumber(assetDetail.getValue());
                }
                inventory.setAssetHeadDTO(assetHead != null ? AssetMapper.toAssetHeadDTO(assetHead) : null);
                inventory.getAssetHeadDTO().setAssetDetails(null);
                //if assetSerialNumber is linked
                //List<String> assetSerialNumbers = new ArrayList<>();
                if (Objects.nonNull(inventory.getProjectInventorySerials())) {
                    if(assetHead.getSerialized().equalsIgnoreCase("Yes")){
                        inventory.setSerialNumberCount(Long.valueOf(inventory.getProjectInventorySerials().size()));
                        //inventory.setQuantity(Long.valueOf(inventory.getProjectInventorySerials().size()));
                    }
                    inventory.setProjectInventorySerials(null);
                      /*inventory.getProjectInventorySerials().forEach(serial-> {
                        AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findById(serial.getAssetSerialNumberId()).get();
                        serial.setSerialNumber(assetSerialNumber.getSerialNumber());
                    });*/
                    //inventory.setAssetSerialNumbers(assetSerialNumbers);
                }
            }
        });
        return projectInventories;
    }

    @Override
    public void deleteById(Long id) {
        projectInventoryRepository.deleteById(id);
    }

    @Override
    public List<ProjectInventorySerial> updateInventorySerials(List<ProjectInventorySerial> projectInventorySerials) {
        ProjectInventory projectInventory = projectInventoryRepository.findById(projectInventorySerials.get(0).getProjectInventoryId()).get();
        projectInventorySerials.forEach(serials-> {
            serials.setProjectInventory(projectInventory);
        });
        return projectInventorySerialRepository.saveAll(projectInventorySerials);
    }

    @Override
    public PagedProjectInventorySerialDTO findAllByProjectInventory(Long projectInventoryId,int pageNumber, Integer pageSize, String sort) {
        Sort sortBy;
        if ("-1".equals(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "-1".equals(sort) ? "createdAt" : sort);
        } else {
            List<String> sortColumns = Arrays.stream(sort.split(",")).collect(Collectors.toList());
            sortBy = Sort.by(sortColumns.get(0));
            for (int i = 1; i < sortColumns.size(); i++) {
                sortBy = sortBy.and(Sort.by(sortColumns.get(i)));
            }
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize == null ? SaasSchema.PAGE_SIZE : pageSize, sortBy);
        ProjectInventory projectInventory = findById(projectInventoryId);
        Page<ProjectInventorySerial> projectInventorySerials = projectInventorySerialRepository.findAllByProjectInventory(pageable, projectInventory);
        if (Objects.nonNull(projectInventorySerials)) {
            projectInventorySerials.forEach(serial-> {
                AssetSerialNumber assetSerialNumber = assetSerialNumberRepository.findById(serial.getAssetSerialNumberId()).get();
                if (assetSerialNumber!=null) {
                    serial.setSerialNumber(assetSerialNumber.getSerialNumber());
                    serial.setPalletNumber(assetSerialNumber.getPalletNo() !=null ? assetSerialNumber.getPalletNo() : "");
                }
            });
            return PagedProjectInventorySerialDTO.builder()
                    .totalItems(projectInventorySerials.getTotalElements())
                    .inventorySerials(ProjectMapper.toProjectInventorySerialDTOs(projectInventorySerials.getContent()))
                    .build();
        }
        return PagedProjectInventorySerialDTO.builder()
                .totalItems(0)
                .inventorySerials(null)
                .build();
    }

    @Override
    public void deleteInventorySerials(List<ProjectInventorySerial> projectInventorySerials) {
        projectInventorySerialRepository.deleteAll(projectInventorySerials);
    }

    @Override
    public void deleteInventorySerialsById(Long id) {
        projectInventorySerialRepository.deleteById(id);
    }

}
