package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.assetHead.AssetMapper;
import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.repository.AssetDetailRepository;
import com.solar.api.tenant.repository.AssetHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.assetHead.AssetMapper.toUpdatedAssetDetail;

@Service
//@Transactional("tenantTransactionManager")
public class AssetDetailServiceImpl implements AssetDetailService {

    @Autowired
    private AssetDetailRepository repository;
    @Autowired
    private AssetHeadRepository assetHeadRepository;

    @Override
    public AssetDetail save(AssetDetail assetDetail) {
        return repository.save(assetDetail);
    }

    @Override
    public List<AssetDetail> update(List<AssetDetail> assetDetails, AssetHead assetHead) {
        List<AssetDetail> assetDetailUpdated = new ArrayList<>();
        assetDetails.forEach(detail -> {
            AssetDetail assetDetailDb = repository.findById(detail.getId()).get();
            assetDetailUpdated.add(AssetMapper.toUpdatedAssetDetail(assetDetailDb, detail));
        });
        AssetHead assetHeadDb = assetHeadRepository.findById(assetHead.getId()).get();
        if (assetDetails.size() !=0 ) {
            assetDetails.forEach(detail -> {
                AssetDetail assetDetailDb = findById(detail.getId());
                assetDetailDb.setAssetHead(assetHeadDb);
                assetDetailDb = toUpdatedAssetDetail(assetDetailDb, detail);
                assetDetailUpdated.add(assetDetailDb);
            });
        }
        return repository.saveAll(assetDetailUpdated);
    }

    @Override
    public AssetDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(AssetDetail.class, id));
    }

    @Override
    public List<AssetDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void deleteAll(List<AssetDetail> assetDetails) {
        repository.deleteAll(assetDetails);
    }
}
