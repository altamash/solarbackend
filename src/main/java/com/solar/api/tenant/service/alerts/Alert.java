package com.solar.api.tenant.service.alerts;

import com.solar.api.saas.service.integration.BaseResponse;

import java.util.List;

public interface Alert {

    List<BaseResponse> generate(Object... params);

    default int getBarSize(int maxSize, double maxScale, double value) {
        return (int) (maxSize * value / maxScale);
    }
}
