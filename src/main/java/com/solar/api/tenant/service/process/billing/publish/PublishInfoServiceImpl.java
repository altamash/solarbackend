package com.solar.api.tenant.service.process.billing.publish;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.PublishInfoRepository;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
//@Transactional("masterTransactionManager")
public class PublishInfoServiceImpl implements PublishInfoService {

    @Autowired
    private UserService userService;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private PublishInfoRepository publishInfoRepository;

    @Override
    public PublishInfo findByStatus(String status) {
        return publishInfoRepository.findByStatus(status);
    }

    @Override
    public PublishInfo save(Long headId) {
        BillingHead billingHead = billingHeadRepository.findById(headId).orElseThrow(() -> new NotFoundException(BillingHead.class, headId));
        User user = userService.findById(billingHead.getUserAccountId());
        PublishInfo publishInfo = PublishInfo.builder()
                .status(EPublishBillStatus.NEW.getStatus())
                .type(EPublishBillType.INVOICE.getType())
                .channel(EPublishBillChannel.EMAIL.getChannel())
                .channelRecipient(user.getEmailAddress())
                .attemptCount(0)
                .referenceId(billingHead.getId())
                .build();
        publishInfoRepository.save(publishInfo);

//        PublishInfo publishInfo = PublishInfo.builder()
//                .status(EPublishBillStatus.NEW.getStatus())
//                .type(EPublishBillType.INVOICE.getType())
//                .channel(EPublishBillChannel.EMAIL.getChannel())
//                .channelRecipient("testna@solarinformatics.com")
//                .attemptCount(0)
//                .referenceId(1L)
//                .build();
        publishInfoRepository.save(publishInfo);
        return publishInfo;
    }

    @Override
    public Long count(PublishInfo publishInfo) {
        return publishInfoRepository.count(Example.of(publishInfo));
    }

    @Override
    public PublishInfo update(Long headId, int status) {
        List<PublishInfo> publishInfo = findByReferenceId(headId);

        if (publishInfo.get(0).getDateSent() == null) {
            publishInfo.get(0).setAttemptCount(1);
            publishInfo.get(0).setStatusNum(status);
            publishInfo.get(0).setDateSent(new Date());
            if (status == 202) {
                publishInfo.get(0).setStatus(EPublishBillStatus.SUCCESS.getStatus());
            } else if (status == 404 || status == 400) {
                publishInfo.get(0).setStatus(EPublishBillStatus.CORRUPTED.getStatus());
            } else {
                publishInfo.get(0).setStatus(EPublishBillStatus.FAILED.getStatus());
            }
            publishInfoRepository.save(publishInfo.get(0));
        }
        return publishInfo.get(0);
    }

    @Override
    public List<PublishInfo> getAll() {
        return publishInfoRepository.findAll();
    }

    @Override
    public List<PublishInfo> findByReferenceId(Long referenceId) {
        return publishInfoRepository.findByReferenceId(referenceId);
    }

    @Override
    public PublishInfo findById(Long id) {
        return publishInfoRepository.getOne(id);
    }

    @Override
    public void deleteAll() {
        publishInfoRepository.deleteAll();
    }
}
