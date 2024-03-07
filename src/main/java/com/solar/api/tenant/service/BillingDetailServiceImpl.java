package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.repository.BillingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class BillingDetailServiceImpl implements BillingDetailService {

    @Autowired
    BillingDetailRepository billingDetailRepository;

    @Autowired
    BillingHeadService billingHeadService;

    @Autowired
    private Utility utility;

    @Override
    public BillingDetail addOrUpdateBillingDetail(BillingDetail billingDetail) {
        int rounding = utility.getCompanyPreference().getRounding();
        if (billingDetail.getId() != null) {
            BillingDetail billingDetailData = findById(billingDetail.getId());
            if (billingDetailData == null) {
                throw new NotFoundException(BillingDetail.class, billingDetail.getId());
            }
            billingDetailData = BillingDetailMapper.toUpdatedBillingDetail(billingDetailData, billingDetail);
            billingDetailData.setValue(utility.roundBilling(billingDetailData.getValue(), rounding));
            return billingDetailRepository.save(billingDetailData);
        }
        billingDetail.setValue(utility.roundBilling(billingDetail.getValue(), rounding));
        return billingDetailRepository.save(billingDetail);
    }

    @Override
    public List<BillingDetail> saveAll(List<BillingDetail> billingDetails) {
        int rounding = utility.getCompanyPreference().getRounding();
        billingDetails.forEach(detail -> detail.setValue(utility.roundBilling(detail.getValue(), rounding)));
        return billingDetailRepository.saveAll(billingDetails);
    }

    @Override
    public Integer findByValue(Double value) {
        return null;
    }

    @Override
    public BillingDetail findById(Long id) {
        return billingDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(BillingDetail.class, id));
    }

    @Override
    public BillingDetail findByBillingHeadAndBillingCode(BillingHead billingHead, String billingCode) {
        return billingDetailRepository.findByBillingHeadAndBillingCode(billingHead, billingCode);
    }

    @Override
    public List<BillingDetail> findByBillingHeadIdAndAddToBillAmount(Long headId, Boolean addToBillAmount) {
        return billingDetailRepository.findByBillingHeadIdAndAddToBillAmount(headId, addToBillAmount);
    }

    @Override
    public List<BillingDetail> findAll() {
        return billingDetailRepository.findAll();
    }

    @Override
    public List<BillingDetail> findByBillingHeadId(Long id) {
        BillingHead billingHead = billingHeadService.findById(id);
        return billingDetailRepository.findByBillingHead(billingHead);
    }

    @Override
    public void delete(Long id) {
        billingDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        billingDetailRepository.deleteAll();
    }

}
