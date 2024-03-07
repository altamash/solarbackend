package com.solar.api.tenant.mapper.billingInvoicing;

import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {

    public static InvoiceLog toInvoiceLog(InvoiceLogDTO invoiceLogDTO) {

        return InvoiceLog.builder()
                .id(invoiceLogDTO.getId())
                .invoiceStatus(invoiceLogDTO.getInvoiceStatus())
                .sequenceNo(invoiceLogDTO.getSequenceNo())
                .fileName(invoiceLogDTO.getFileName())
                .uniqueCode(invoiceLogDTO.getUniqueCode())
                .passcode(invoiceLogDTO.getPasscode())
                .fileIntegrityCheck(invoiceLogDTO.getFileIntegrityCheck())
                .emailedIndicator(invoiceLogDTO.getEmailedIndicator())
                .email(invoiceLogDTO.getEmail())
                .emailStatus(invoiceLogDTO.getEmailStatus())
                .emailContent(invoiceLogDTO.getEmailContent())
                .emailDateTime(invoiceLogDTO.getEmailDateTime())
                .message(invoiceLogDTO.getMessage())
                .variantInvoiceLog(invoiceLogDTO.getVariantInvoiceLog())
                .build();
    }

    public static InvoiceLogDTO toInvoiceLogDTO(InvoiceLog invoiceLog) {

        if (invoiceLog == null) {
            return null;
        }
        return InvoiceLogDTO.builder()
                .id(invoiceLog.getId())
                .invoiceStatus(invoiceLog.getInvoiceStatus())
                .sequenceNo(invoiceLog.getSequenceNo())
                .fileName(invoiceLog.getFileName())
                .uniqueCode(invoiceLog.getUniqueCode())
                .passcode(invoiceLog.getPasscode())
                .fileIntegrityCheck(invoiceLog.getFileIntegrityCheck())
                .emailedIndicator(invoiceLog.getEmailedIndicator())
                .email(invoiceLog.getEmail())
                .emailStatus(invoiceLog.getEmailStatus())
                .emailContent(invoiceLog.getEmailContent())
                .emailDateTime(invoiceLog.getEmailDateTime())
                .message(invoiceLog.getMessage())
                .variantInvoiceLog(invoiceLog.getVariantInvoiceLog())
                .build();
    }

    public static InvoiceLog toUpdatedInvoiceLog(InvoiceLog invoiceLog, InvoiceLog invoiceLogUpdate) {
        invoiceLog.setInvoiceStatus(invoiceLogUpdate.getInvoiceStatus() == null ? invoiceLog.getInvoiceStatus() :
                invoiceLogUpdate.getInvoiceStatus());
        invoiceLog.setSequenceNo(invoiceLogUpdate.getSequenceNo() == null ? invoiceLog.getSequenceNo() :
                invoiceLogUpdate.getSequenceNo());
        invoiceLog.setFileName(invoiceLogUpdate.getFileName() == null ? invoiceLog.getFileName() :
                invoiceLogUpdate.getFileName());
        invoiceLog.setUniqueCode(invoiceLogUpdate.getUniqueCode() == null ? invoiceLog.getUniqueCode() :
                invoiceLogUpdate.getUniqueCode());
        invoiceLog.setPasscode(invoiceLogUpdate.getPasscode() == null ? invoiceLog.getPasscode() :
                invoiceLogUpdate.getPasscode());
        invoiceLog.setFileIntegrityCheck(invoiceLogUpdate.getFileIntegrityCheck() == null ? invoiceLog.getFileIntegrityCheck() :
                invoiceLogUpdate.getFileIntegrityCheck());
        invoiceLog.setEmailedIndicator(invoiceLogUpdate.getEmailedIndicator() == null ? invoiceLog.getEmailedIndicator() :
                invoiceLogUpdate.getEmailedIndicator());
        invoiceLog.setEmail(invoiceLogUpdate.getEmail() == null ?
                invoiceLog.getEmail() : invoiceLogUpdate.getEmail());
        invoiceLog.setEmailContent(invoiceLogUpdate.getEmailContent() == null ?
                invoiceLog.getEmailContent() : invoiceLogUpdate.getEmailContent());
        invoiceLog.setEmailDateTime(invoiceLogUpdate.getEmailDateTime() == null ?
                invoiceLog.getEmailDateTime() : invoiceLogUpdate.getEmailDateTime());
        invoiceLog.setMessage(invoiceLogUpdate.getMessage() == null ?
                invoiceLog.getMessage() : invoiceLogUpdate.getMessage());
        invoiceLog.setVariantInvoiceLog(invoiceLogUpdate.getVariantInvoiceLog() == null ?
                invoiceLog.getVariantInvoiceLog() : invoiceLogUpdate.getVariantInvoiceLog());
        return invoiceLog;
    }

    public static List<InvoiceLog> toInvoiceLogs(List<InvoiceLogDTO> invoiceLogDTOS) {
        return invoiceLogDTOS.stream().map(bh -> toInvoiceLog(bh)).collect(Collectors.toList());
    }

    public static List<InvoiceLogDTO> toInvoiceLogDTOs(List<InvoiceLog> invoiceLogs) {
        return invoiceLogs.stream().map(
                bh -> toInvoiceLogDTO(bh)).collect(Collectors.toList());
    }
}
