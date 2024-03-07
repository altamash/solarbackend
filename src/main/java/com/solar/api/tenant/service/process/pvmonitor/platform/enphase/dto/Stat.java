package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stat {
    private List<Integer> production;
    private Production totals;

    @Override
    public String toString() {
        return "Stat{" +
                "production=" + production +
                ", totals=" + totals +
                '}';
    }
}
