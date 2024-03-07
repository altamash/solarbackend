package com.solar.api.tenant.service.contract.validation;

import com.solar.api.exception.NotFoundException;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ValidationFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public IContractValidation get(int tenantTier) {
        switch (tenantTier) {
            case 1: {
                return applicationContext.getBean(TierOneContractValidation.class);
            }
            case 2: {
                return applicationContext.getBean(TierTwoContractValidation.class);
            }
            default: {
                throw new NotFoundException(IContractValidation.class, "Tenant tier", String.valueOf(tenantTier));
            }
        }
    }
}
