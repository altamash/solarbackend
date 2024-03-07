package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.report.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
    ReportTemplate findByTemplateNameAndOutputFormat(String templateName, String outputFormat);
}
