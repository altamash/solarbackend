package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.tenant.mapper.extended.assetHead.AssetMapper;
import com.solar.api.tenant.mapper.extended.assetHead.PagedAssetSerialNumberDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import com.solar.api.tenant.repository.project.ProjectInventorySerialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class AssetSerialNumberServiceImpl implements AssetSerialNumberService {

    @Autowired
    private AssetSerialNumberRepository repository;
    @Autowired
    private ProjectInventorySerialRepository projectInventorySerialRepository;
    @Autowired
    private AssetHeadRepository assetHeadRepository;

    @Override
    public AssetSerialNumber save(AssetSerialNumber assetSerialNumber) {
        return repository.save(assetSerialNumber);
    }

    @Override
    public AssetSerialNumber update(AssetSerialNumber assetSerialNumber) {
        return repository.save(assetSerialNumber);
    }

    @Override
    public AssetSerialNumber findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(AssetSerialNumber.class, id));
    }

    @Override
    public List<AssetSerialNumber> findAll() {
        return repository.findAll();
    }

    @Override
    public List<AssetSerialNumber> findAllByAssetId(Long assetId) {
        return repository.findAllByAssetId(assetId);
    }

    @Override
    public List<AssetSerialNumber> findFilteredSerialByAssetId(Long assetId) {
        List<AssetSerialNumber> assetSerialNumbers = repository.findAllByAssetId(assetId);
        if (assetSerialNumbers.size() !=0 ) {
            List<Long> assetSerialIds = assetSerialNumbers.stream().map(AssetSerialNumber::getId).collect(Collectors.toList());
            List<ProjectInventorySerial> projectInventorySerials = projectInventorySerialRepository.findAllByAssetSerialNumberIdIn(assetSerialIds);
            if (projectInventorySerials.size() !=0 ) {
                List<Long> invSerials = projectInventorySerials.stream().map(ProjectInventorySerial::getAssetSerialNumberId).collect(Collectors.toList());
                assetSerialNumbers = assetSerialNumbers.stream().filter(x-> !invSerials.contains(x.getId())).collect(Collectors.toList());
            }
        }
        return assetSerialNumbers;
    }

    @Override
    public PagedAssetSerialNumberDTO findPagedFilteredSerialByAssetId(Long assetId, int pageNumber, Integer pageSize, String sort) {
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
        AssetHead assetHead = assetHeadRepository.findById(assetId).orElse(null);
        List<AssetSerialNumber> assetSerialNumbers = repository.findAllByAssetId(assetId);
        Page<AssetSerialNumber> finalAssetSerials = null;
        if (assetSerialNumbers.size() !=0 ) {
            int totalElements = 0; int fromIndex = 0; int toIndex = 10;
            List<AssetSerialNumber> indexObjects = null;

            List<Long> assetSerialIds = assetSerialNumbers.stream().map(AssetSerialNumber::getId).collect(Collectors.toList());
            List<ProjectInventorySerial> projectInventorySerials = projectInventorySerialRepository.findAllByAssetSerialNumberIdIn(assetSerialIds);
            if (projectInventorySerials.size() !=0 ) {
                List<Long> invSerials = projectInventorySerials.stream().map(ProjectInventorySerial::getAssetSerialNumberId).collect(Collectors.toList());
                assetSerialNumbers = assetSerialNumbers.stream().filter(x-> !invSerials.contains(x.getId())).collect(Collectors.toList());
                totalElements = assetSerialNumbers.size();
                if (pageable == null) {
                    pageable = PageRequest.of(0, 10);
                }
                fromIndex = pageable.getPageSize() * pageable.getPageNumber();
                toIndex = pageable.getPageSize() * (pageable.getPageNumber() + 1);
                if (toIndex > totalElements) {
                    toIndex = totalElements;
                }
                indexObjects = assetSerialNumbers.subList(fromIndex, toIndex);
                finalAssetSerials = new PageImpl<AssetSerialNumber>(indexObjects
                        , pageable, totalElements);

                return PagedAssetSerialNumberDTO.builder()
                        .totalItems(finalAssetSerials.getTotalElements())
                        .serialized(assetHead.getSerialized())
                        .assetSerialNumberDTOS(AssetMapper.toAssetSerialNumbersDTOs(finalAssetSerials.getContent())).build();
            }

            totalElements = assetSerialNumbers.size();
            if (pageable == null) {
                pageable = PageRequest.of(0, 10);
            }
            fromIndex = pageable.getPageSize() * pageable.getPageNumber();
            toIndex = pageable.getPageSize() * (pageable.getPageNumber() + 1);
            if (toIndex > totalElements) {
                toIndex = totalElements;
            }
            indexObjects = assetSerialNumbers.subList(fromIndex, toIndex);
            finalAssetSerials = new PageImpl<AssetSerialNumber>(indexObjects
                    , pageable, totalElements);

            return PagedAssetSerialNumberDTO.builder()
                    .totalItems(finalAssetSerials.getTotalElements())
                    .serialized(assetHead.getSerialized())
                    .assetSerialNumberDTOS(AssetMapper.toAssetSerialNumbersDTOs(finalAssetSerials.getContent())).build();
        }

        return PagedAssetSerialNumberDTO.builder()
                .totalItems(0)
                .serialized(assetHead != null ? assetHead.getSerialized() : "Asset Id: "+assetId + " doesn't exists.")
                .assetSerialNumberDTOS(null).build();
    }

    @Override
    public PagedAssetSerialNumberDTO searchSerialNoByPalletNoAndAssetId(Long assetId, String palletNo, int pageNumber, Integer pageSize, String sort) {
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
        AssetHead assetHead = assetHeadRepository.findById(assetId).orElse(null);
        List<AssetSerialNumber> assetSerialNumbers = null;

        if (!palletNo.equals("-1")) {
            assetSerialNumbers = repository.findAllByAssetIdAndPalletNo(assetId,palletNo);

        } else {
            assetSerialNumbers = repository.findAllByAssetId(assetId);
        }

        Page<AssetSerialNumber> finalAssetSerials = null;
        if (assetSerialNumbers.size() !=0 ) {
            int totalElements = 0; int fromIndex = 0; int toIndex = 10;
            List<AssetSerialNumber> indexObjects = null;

            List<Long> assetSerialIds = assetSerialNumbers.stream().map(AssetSerialNumber::getId).collect(Collectors.toList());
            List<ProjectInventorySerial> projectInventorySerials = projectInventorySerialRepository.findAllByAssetSerialNumberIdIn(assetSerialIds);
            if (projectInventorySerials.size() !=0 ) {
                List<Long> invSerials = projectInventorySerials.stream().map(ProjectInventorySerial::getAssetSerialNumberId).collect(Collectors.toList());
                assetSerialNumbers = assetSerialNumbers.stream().filter(x-> !invSerials.contains(x.getId())).collect(Collectors.toList());
                totalElements = assetSerialNumbers.size();
                if (pageable == null) {
                    pageable = PageRequest.of(0, 10);
                }
                fromIndex = pageable.getPageSize() * pageable.getPageNumber();
                toIndex = pageable.getPageSize() * (pageable.getPageNumber() + 1);
                if (toIndex > totalElements) {
                    toIndex = totalElements;
                }
                indexObjects = assetSerialNumbers.subList(fromIndex, toIndex);
                finalAssetSerials = new PageImpl<AssetSerialNumber>(indexObjects
                        , pageable, totalElements);

                return PagedAssetSerialNumberDTO.builder()
                        .totalItems(finalAssetSerials.getTotalElements())
                        .serialized(assetHead.getSerialized())
                        .assetSerialNumberDTOS(AssetMapper.toAssetSerialNumbersDTOs(finalAssetSerials.getContent())).build();
            }

            totalElements = assetSerialNumbers.size();
            if (pageable == null) {
                pageable = PageRequest.of(0, 10);
            }
            fromIndex = pageable.getPageSize() * pageable.getPageNumber();
            toIndex = pageable.getPageSize() * (pageable.getPageNumber() + 1);
            if (toIndex > totalElements) {
                toIndex = totalElements;
            }
            indexObjects = assetSerialNumbers.subList(fromIndex, toIndex);
            finalAssetSerials = new PageImpl<AssetSerialNumber>(indexObjects
                    , pageable, totalElements);

            return PagedAssetSerialNumberDTO.builder()
                    .totalItems(finalAssetSerials.getTotalElements())
                    .serialized(assetHead.getSerialized())
                    .assetSerialNumberDTOS(AssetMapper.toAssetSerialNumbersDTOs(finalAssetSerials.getContent())).build();
        }

        return PagedAssetSerialNumberDTO.builder()
                .totalItems(0)
                .serialized(assetHead != null ? assetHead.getSerialized() : "Pallet record not found: "+palletNo )
                .assetSerialNumberDTOS(null).build();
    }

    @Override
    public List<String> distinctPalletNumbersByAssetId(Long assetId) {
        return repository.palletNumbersByAssetId(assetId);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
