package com.solar.api.saas.mapper.process.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedJobManagerDTO {
    long totalItems;
    List<JobManagerDTO> jobs;
}
