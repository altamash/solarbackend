package com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDTO {

    private List<RecordDTO> records;
}
