package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.process.pvmonitor.platform.egauge.EGaugeAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.enphase.EnphaseAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.GoodWeAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.solaredge.SolarEdgeAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.solax.SolaxAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.solis.SolisAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.solrenview.SolrenviewAPI;
import com.solar.api.tenant.service.process.pvmonitor.platform.tigo.TigoAPI;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ExternalAPIFactory implements ApplicationContextAware {

    @Autowired
    private UserService userService;

    private ApplicationContext applicationContext;

    /**
     * Updated at: 3rd July, 2023
     * By Shariq
     *
     * @param mp
     * @return
     */
    public MonitorAPI get(String mp) {

        //Making it all lowerCase to avoid NULL
        mp = mp.toLowerCase();
        switch (mp) {
            case "solax":
                return applicationContext.getBean(SolaxAPI.class);
            case "solis":
                return applicationContext.getBean(SolisAPI.class);
            case "goodwe":
                return applicationContext.getBean(GoodWeAPI.class);
            case "egauge":
                return applicationContext.getBean(EGaugeAPI.class);
            case "solrenview":
                return applicationContext.getBean(SolrenviewAPI.class);
            case "solaredge":
                return applicationContext.getBean(SolarEdgeAPI.class);
            case "tigo":
                return applicationContext.getBean(TigoAPI.class);
            case "enphase":
                return applicationContext.getBean(EnphaseAPI.class);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
