package com.solar.api.tenant.service.process.billing.billingDiscount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.repository.BillingDetailRepository;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.BillingHeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class BillingDiscount {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    BillingDetailService billingDetailService;

    @Autowired
    BillingDetailRepository billingDetailRepository;

    @Autowired
    BillingHeadService billingHeadService;

    Integer maxLine = 0;

    /**
     * @param billingDiscountDTO
     * @return
     */
    public ObjectNode addDiscount(BillingDiscountDTO billingDiscountDTO) {
        ObjectNode returnJSON = new ObjectMapper().createObjectNode();
        BillingHead billingHead = billingHeadService.findById(billingDiscountDTO.getHeadId());
        if (billingHead != null) {
            Integer getMaxLine = billingDetailRepository.getMaxLineSeq(billingHead);
            if (getMaxLine != null) {
                maxLine = getMaxLine;
            }
            BillingDetail codeIsExist = billingDetailRepository.findByBillingHeadAndBillingCode(billingHead,
                    billingDiscountDTO.getBillingCode());
            if (codeIsExist != null) {
                codeIsExist.setAddToBillAmount(true);
                codeIsExist.setBillingCode(billingDiscountDTO.getBillingCode());
                codeIsExist.setBillingHead(billingHead);
                codeIsExist.setNotes(billingDiscountDTO.getNotes());
                codeIsExist.setValue(billingDiscountDTO.getValue());
                codeIsExist.setLineSeqNo(codeIsExist.getLineSeqNo());
                billingDetailService.addOrUpdateBillingDetail(codeIsExist);
            } else {
                BillingDetail billingDetail = new BillingDetail();
                billingDetail.setAddToBillAmount(true);
                billingDetail.setBillingCode(billingDiscountDTO.getBillingCode());
                billingDetail.setBillingHead(billingHead);
                billingDetail.setNotes(billingDiscountDTO.getNotes());
                billingDetail.setValue(billingDiscountDTO.getValue());
                billingDetail.setLineSeqNo(maxLine + 1);
                billingDetailService.addOrUpdateBillingDetail(billingDetail);
            }
        } else {
            throw new NotFoundException(BillingHead.class, billingHead.getId());
        }
        returnJSON.put("Discount Added", billingDiscountDTO.getValue());
        return returnJSON;
    }

    /**
     * Description: Method to add discount to bills
     * @param billingDiscountDTO
     * @return
     */
    public Map addDiscountV1(BillingDiscountDTO billingDiscountDTO) {
        Map response = new HashMap();
        try {
            BillingHead billingHead = billingHeadService.findById(billingDiscountDTO.getHeadId());

            Integer getMaxLine = billingDetailRepository.getMaxLineSeq(billingHead);
            if (getMaxLine != null) {
                maxLine = getMaxLine;
            }
            BillingDetail codeIsExist = billingDetailRepository.findByBillingHeadAndBillingCode(billingHead,
                    billingDiscountDTO.getBillingCode());
            if (codeIsExist != null) {
                codeIsExist.setAddToBillAmount(true);
                codeIsExist.setBillingCode(billingDiscountDTO.getBillingCode());
                codeIsExist.setBillingHead(billingHead);
                codeIsExist.setNotes(billingDiscountDTO.getNotes());
                codeIsExist.setValue(billingDiscountDTO.getValue());
                codeIsExist.setLineSeqNo(codeIsExist.getLineSeqNo());
                billingDetailService.addOrUpdateBillingDetail(codeIsExist);
            } else {
                BillingDetail billingDetail = new BillingDetail();
                billingDetail.setAddToBillAmount(true);
                billingDetail.setBillingCode(billingDiscountDTO.getBillingCode());
                billingDetail.setBillingHead(billingHead);
                billingDetail.setNotes(billingDiscountDTO.getNotes());
                billingDetail.setValue(billingDiscountDTO.getValue());
                billingDetail.setLineSeqNo(maxLine + 1);
                billingDetailService.addOrUpdateBillingDetail(billingDetail);
            }
            response.put("code", HttpStatus.OK);
            response.put("message", "Discount Added " + billingDiscountDTO.getValue());
            response.put("data", null);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.put("data", null);
            response.put("message", "Bill Head with ID: " + billingDiscountDTO.getHeadId() + " Not Found");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
        }

        return response;
    }

    /**
     * Description: Method for bulk discount of bills
     * @param billingDiscountDTO
     * @param billingHeadIds
     */
    @Async
    public void bulkAddDiscountV1(BillingDiscountDTO billingDiscountDTO,String billingHeadIds) {
        List<Long> rowIds =
                Arrays.stream(billingHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        try {
        List<BillingHead> billingHeadList = billingHeadService.findAllByIds(rowIds);
        billingHeadList.forEach(billingHead ->{
                Integer getMaxLine = billingDetailRepository.getMaxLineSeq(billingHead);
                if (getMaxLine != null) {
                    maxLine = getMaxLine;
                }
                BillingDetail codeIsExist = billingDetailRepository.findByBillingHeadAndBillingCode(billingHead,
                        billingDiscountDTO.getBillingCode());
                if (codeIsExist != null) {
                    codeIsExist.setAddToBillAmount(true);
                    codeIsExist.setBillingCode(billingDiscountDTO.getBillingCode());
                    codeIsExist.setBillingHead(billingHead);
                    codeIsExist.setNotes(billingDiscountDTO.getNotes());
                    codeIsExist.setValue(-billingDiscountDTO.getValue());
                    codeIsExist.setLineSeqNo(codeIsExist.getLineSeqNo());
                    billingDetailService.addOrUpdateBillingDetail(codeIsExist);
                } else {
                    BillingDetail billingDetail = new BillingDetail();
                    billingDetail.setAddToBillAmount(true);
                    billingDetail.setBillingCode(billingDiscountDTO.getBillingCode());
                    billingDetail.setBillingHead(billingHead);
                    billingDetail.setNotes(billingDiscountDTO.getNotes());
                    billingDetail.setValue(-billingDiscountDTO.getValue());
                    billingDetail.setLineSeqNo(maxLine + 1);
                    billingDetailService.addOrUpdateBillingDetail(billingDetail);
                }

                });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
