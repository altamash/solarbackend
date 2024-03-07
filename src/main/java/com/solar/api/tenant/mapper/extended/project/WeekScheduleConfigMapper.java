package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.model.extended.project.WeekScheduleConfig;

public class WeekScheduleConfigMapper {

    public static WeekScheduleConfigDTO toWeekScheduleConfigDTO(WeekScheduleConfig weekScheduleConfig) {
        if (weekScheduleConfig == null) {
            return null;
        }
        return WeekScheduleConfigDTO.builder()
                .id(weekScheduleConfig.getId())
                .description(weekScheduleConfig.getDescription())
                .projectId(weekScheduleConfig.getProjectId())
                .weekName(weekScheduleConfig.getWeekName())
                .mon(weekScheduleConfig.getMon())
                .tue(weekScheduleConfig.getTue())
                .wed(weekScheduleConfig.getWed())
                .thu(weekScheduleConfig.getThu())
                .fri(weekScheduleConfig.getFri())
                .sat(weekScheduleConfig.getSat())
                .sun(weekScheduleConfig.getSun())
                .build();
    }

    public static WeekScheduleConfig toWeekScheduleConfig(WeekScheduleConfigDTO weekScheduleConfigDTO) {
        if (weekScheduleConfigDTO == null) {
            return null;
        }
        return WeekScheduleConfig.builder()
                .id(weekScheduleConfigDTO.getId())
                .description(weekScheduleConfigDTO.getDescription())
                .projectId(weekScheduleConfigDTO.getProjectId())
                .weekName(weekScheduleConfigDTO.getWeekName())
                .mon(weekScheduleConfigDTO.getMon())
                .tue(weekScheduleConfigDTO.getTue())
                .wed(weekScheduleConfigDTO.getWed())
                .thu(weekScheduleConfigDTO.getThu())
                .fri(weekScheduleConfigDTO.getFri())
                .sat(weekScheduleConfigDTO.getSat())
                .sun(weekScheduleConfigDTO.getSun())
                .build();
    }
}
