package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIBody {
    private Integer pageNo;
    private Integer pageSize;
    private String states;
    private String language;
    private String beginTime;
    private String sn;

    @Override
    public String toString() {
        return "PVMonitorAPIBody{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", states='" + states + '\'' +
                ", language='" + language + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }
}
