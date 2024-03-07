package com.solar.api.tenant.mapper.publishInfo;

import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfoArchive;

import java.util.List;
import java.util.stream.Collectors;

public class PublishInfoMapper {

    public static PublishInfoArchive toPublishInfo(PublishInfo publishInfo) {

        return PublishInfoArchive.builder()
                .attemptCount(publishInfo.getAttemptCount())
                .channel(publishInfo.getChannel())
                .channelRecipient(publishInfo.getChannelRecipient())
                .referenceId(publishInfo.getReferenceId())
                .status(publishInfo.getStatus())
                .type(publishInfo.getType())
                .dateSent(publishInfo.getDateSent())
                .build();
    }

    public static List<PublishInfoArchive> toPublishInfo(List<PublishInfo> publishInfos) {
        return publishInfos.stream().map(pi -> toPublishInfo(pi)).collect(Collectors.toList());
    }
}
