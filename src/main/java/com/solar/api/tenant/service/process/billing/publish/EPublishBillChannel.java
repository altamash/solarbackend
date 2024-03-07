package com.solar.api.tenant.service.process.billing.publish;

import java.util.Arrays;

public enum EPublishBillChannel {

    EMAIL("EMAIL"),
    SMS("SMS");

    String channel;

    EPublishBillChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public static EPublishBillChannel get(String channel) {
        return Arrays.stream(values()).filter(value -> channel.equalsIgnoreCase(value.channel)).findFirst().orElse(null);
    }
}
