package com.solar.api.tenant.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Integer EXPIRE_MINS = 1;
    private LoadingCache<String, Integer> otpCache;

    public OtpServiceImpl(){
        super();
        otpCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    @Override
    public Integer generateOTP(String userId, String requestType) {
        Random random = new Random();
        Integer codeExists = getOtp(userId);
        if (codeExists != 0) {
            clearOTP(userId);
        }
        String otpCode = String.format("%06d", 100000 + random.nextInt(900000));
        Integer otp = Integer.parseInt(otpCode);
        otpCache.put(userId, otp);
        return otp;
    }

    @Override
    public Integer getOtp(String userId){
        try{
            return otpCache.get(userId);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public void clearOTP(String userId){
        otpCache.invalidate(userId);
    }

}
