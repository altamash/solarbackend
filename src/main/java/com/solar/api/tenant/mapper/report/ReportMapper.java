package com.solar.api.tenant.mapper.report;

import com.solar.api.tenant.model.report.ReportIteratorDefinition;
import com.solar.api.tenant.model.report.ReportTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMapper {

    // ReportTemplate ////////////////////////////////////////////////
    public static ReportTemplate toReportTemplate(ReportTemplateDTO reportTemplateDTO) {
        if (reportTemplateDTO == null) {
            return null;
        }
        return ReportTemplate.builder()
                .id(reportTemplateDTO.getId())
                .templateName(reportTemplateDTO.getTemplateName())
                .category(reportTemplateDTO.getCategory())
                .type(reportTemplateDTO.getType())
                .outputFormat(reportTemplateDTO.getOutputFormat())
                .templateURI(reportTemplateDTO.getTemplateURI())
                .permission(reportTemplateDTO.getPermission())
                .build();
    }

    public static ReportTemplateDTO toReportTemplateDTO(ReportTemplate reportTemplate) {
        if (reportTemplate == null) {
            return null;
        }
        return ReportTemplateDTO.builder()
                .id(reportTemplate.getId())
                .templateName(reportTemplate.getTemplateName())
                .category(reportTemplate.getCategory())
                .type(reportTemplate.getType())
                .outputFormat(reportTemplate.getOutputFormat())
                .templateURI(reportTemplate.getTemplateURI())
                .permission(reportTemplate.getPermission())
                .createdAt(reportTemplate.getCreatedAt())
                .updatedAt(reportTemplate.getUpdatedAt())
                .build();
    }

    public static ReportTemplate toUpdatedReportTemplate(ReportTemplate reportTemplate,
                                                         ReportTemplate reportTemplateUpdate) {
        reportTemplate.setTemplateName(reportTemplateUpdate.getTemplateName() == null ?
                reportTemplate.getTemplateName() : reportTemplateUpdate.getTemplateName());
        reportTemplate.setCategory(reportTemplateUpdate.getCategory() == null ? reportTemplate.getCategory() :
                reportTemplateUpdate.getCategory());
        reportTemplate.setType(reportTemplateUpdate.getType() == null ? reportTemplate.getType() :
                reportTemplateUpdate.getType());
        reportTemplate.setOutputFormat(reportTemplateUpdate.getOutputFormat() == null ?
                reportTemplate.getOutputFormat() : reportTemplateUpdate.getOutputFormat());
        reportTemplate.setTemplateURI(reportTemplateUpdate.getTemplateURI() == null ?
                reportTemplate.getTemplateURI() : reportTemplateUpdate.getTemplateURI());
        reportTemplate.setPermission(reportTemplateUpdate.getPermission() == null ? reportTemplate.getPermission() :
                reportTemplateUpdate.getPermission());
        return reportTemplate;
    }

    public static List<ReportTemplate> toReportTemplates(List<ReportTemplateDTO> reportTemplateDTOs) {
        return reportTemplateDTOs.stream().map(r -> toReportTemplate(r)).collect(Collectors.toList());
    }

    public static List<ReportTemplateDTO> toReportTemplateDTOs(List<ReportTemplate> reportTemplates) {
        return reportTemplates.stream().map(r -> toReportTemplateDTO(r)).collect(Collectors.toList());
    }

    // ReportIteratorDefinition ////////////////////////////////////////////////
    public static ReportIteratorDefinition toReportIteratorDefinition(ReportIteratorDefinitionDTO reportIteratorDefinitionDTO) {
        if (reportIteratorDefinitionDTO == null) {
            return null;
        }
        return ReportIteratorDefinition.builder()
                .id(reportIteratorDefinitionDTO.getId())
                .iteratorName(reportIteratorDefinitionDTO.getIteratorName())
                .filterCodes(reportIteratorDefinitionDTO.getFilterCodes())
                .templateName(reportIteratorDefinitionDTO.getTemplateName())
                .build();
    }

    public static ReportIteratorDefinitionDTO toReportIteratorDefinitionDTO(ReportIteratorDefinition reportIteratorDefinition) {
        if (reportIteratorDefinition == null) {
            return null;
        }
        return ReportIteratorDefinitionDTO.builder()
                .id(reportIteratorDefinition.getId())
                .iteratorName(reportIteratorDefinition.getIteratorName())
                .filterCodes(reportIteratorDefinition.getFilterCodes())
                .templateName(reportIteratorDefinition.getTemplateName())
                .createdAt(reportIteratorDefinition.getCreatedAt())
                .updatedAt(reportIteratorDefinition.getUpdatedAt())
                .build();
    }

    public static ReportIteratorDefinition toUpdatedReportIteratorDefinition(ReportIteratorDefinition reportIteratorDefinition, ReportIteratorDefinition reportIteratorDefinitionUpdate) {
        reportIteratorDefinition.setIteratorName(reportIteratorDefinitionUpdate.getIteratorName() == null ?
                reportIteratorDefinition.getIteratorName() : reportIteratorDefinitionUpdate.getIteratorName());
        reportIteratorDefinition.setFilterCodes(reportIteratorDefinitionUpdate.getFilterCodes() == null ?
                reportIteratorDefinition.getFilterCodes() : reportIteratorDefinitionUpdate.getFilterCodes());
        reportIteratorDefinition.setTemplateName(reportIteratorDefinitionUpdate.getTemplateName() == null ?
                reportIteratorDefinition.getTemplateName() : reportIteratorDefinitionUpdate.getTemplateName());
        return reportIteratorDefinition;
    }

    public static List<ReportIteratorDefinition> toReportIteratorDefinitions(List<ReportIteratorDefinitionDTO> reportIteratorDefinitionDTOs) {
        return reportIteratorDefinitionDTOs.stream().map(r -> toReportIteratorDefinition(r)).collect(Collectors.toList());
    }

    public static List<ReportIteratorDefinitionDTO> toReportIteratorDefinitionDTOs(List<ReportIteratorDefinition> reportIteratorDefinitions) {
        return reportIteratorDefinitions.stream().map(r -> toReportIteratorDefinitionDTO(r)).collect(Collectors.toList());
    }
}
