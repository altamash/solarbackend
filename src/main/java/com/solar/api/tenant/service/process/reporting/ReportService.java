package com.solar.api.tenant.service.process.reporting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.report.ReportIteratorDefinition;
import com.solar.api.tenant.model.report.ReportTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ReportService {

    // ReportTemplate /////////////////////////////////////
    ReportTemplate saveOrUpdate(MultipartFile file, ReportTemplate reportTemplate, String container, Long compKey) throws URISyntaxException, StorageException, IOException;

    List<ReportTemplate> save(List<ReportTemplate> reportTemplates);

    ReportTemplate findById(Long id);

    ReportTemplate findByTemplateNameAndOutputFormat(String templateName, String outputFormat);

    List<ReportTemplate> findAll();

    void delete(Long id);

    void deleteAll();

    // ReportIteratorDefinition /////////////////////////////////////
    ReportIteratorDefinition saveOrUpdateReportIteratorDefinition(ReportIteratorDefinition reportIteratorDefinition);

    List<ReportIteratorDefinition> saveReportIteratorDefinition(List<ReportIteratorDefinition> reportIteratorDefinitions);

    ReportIteratorDefinition findReportIteratorDefinitionById(Long id);

    List<ReportIteratorDefinition> findAllReportIteratorDefinitions();

    void deleteReportIteratorDefinition(Long id);

    void deleteAllReportIteratorDefinition();

    ObjectNode publishInvoice(Long billHeadId);
    ObjectNode publishHTMLInvoice(BillingHead billingHead, CalculationDetails calculationDetails);
}
