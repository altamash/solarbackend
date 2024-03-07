package com.solar.api.tenant.service;

public interface OtpService {

    public Integer generateOTP(String userId, String requestType);
    public Integer getOtp(String userId);
    public void clearOTP(String userId);
}
