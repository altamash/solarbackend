package com.solar.api.tenant.mapper.workflows;

import com.solar.api.tenant.model.workflow.MessageTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class MessageTemplateMapper {

    public static MessageTemplate toMessageTemplate(MessageTemplateDTO messageTemplateDTO) {
        if (messageTemplateDTO == null) {
            return null;
        }
        return MessageTemplate.builder()
                .id(messageTemplateDTO.getId())
                .msgTemplRefId(messageTemplateDTO.getMsgTemplRefId())
                .msgTmplName(messageTemplateDTO.getMsgTmplName())
                .messageType(messageTemplateDTO.getMessageType())
                .parentTmplId(messageTemplateDTO.getParentTmplId())
                .format(messageTemplateDTO.getFormat())
                .templateHTMLCode(messageTemplateDTO.getTemplateHTMLCode())
                .templateStructure(messageTemplateDTO.getTemplateStructure())
                .actionsCode(messageTemplateDTO.getActionsCode())
                .returnMessage(messageTemplateDTO.getReturnMessage())
                .subject(messageTemplateDTO.getSubject())
                .header(messageTemplateDTO.getHeader())
                .footer(messageTemplateDTO.getFooter())
                .build();
    }


    public static MessageTemplateDTO toMessageTemplateDTO(MessageTemplate messageTemplate) {
        if (messageTemplate == null) {
            return null;
        }
        return MessageTemplateDTO.builder()
                .id(messageTemplate.getId())
                .msgTemplRefId(messageTemplate.getMsgTemplRefId())
                .msgTmplName(messageTemplate.getMsgTmplName())
                .messageType(messageTemplate.getMessageType())
                .parentTmplId(messageTemplate.getParentTmplId())
                .format(messageTemplate.getFormat())
                .templateHTMLCode(messageTemplate.getTemplateHTMLCode())
                .templateStructure(messageTemplate.getTemplateStructure())
                .actionsCode(messageTemplate.getActionsCode())
                .returnMessage(messageTemplate.getReturnMessage())
                .subject(messageTemplate.getSubject())
                .header(messageTemplate.getHeader())
                .footer(messageTemplate.getFooter())
                .build();
    }

    public static MessageTemplate toUpdatedMessageTemplate(MessageTemplate messageTemplate, MessageTemplate messageTemplateUpdate) {
        messageTemplate.setId(messageTemplateUpdate.getId() == null ? messageTemplate.getId() : messageTemplateUpdate.getId());
        messageTemplate.setMsgTemplRefId(messageTemplateUpdate.getMsgTemplRefId() == null ? messageTemplate.getMsgTemplRefId() : messageTemplateUpdate.getMsgTemplRefId());
        messageTemplate.setMsgTmplName(messageTemplateUpdate.getMsgTmplName() == null ? messageTemplate.getMsgTmplName() : messageTemplateUpdate.getMsgTmplName());
        messageTemplate.setMessageType(messageTemplateUpdate.getMessageType() == null ? messageTemplate.getMessageType() : messageTemplateUpdate.getMessageType());
        messageTemplate.setParentTmplId(messageTemplateUpdate.getParentTmplId() == null ? messageTemplate.getParentTmplId() : messageTemplateUpdate.getParentTmplId());
        messageTemplate.setFormat(messageTemplateUpdate.getFormat() == null ? messageTemplate.getFormat() : messageTemplateUpdate.getFormat());
        messageTemplate.setTemplateHTMLCode(messageTemplateUpdate.getTemplateHTMLCode() == null ? messageTemplate.getTemplateHTMLCode() : messageTemplateUpdate.getTemplateHTMLCode());
        messageTemplate.setTemplateStructure(messageTemplateUpdate.getTemplateStructure() == null ? messageTemplate.getTemplateStructure() : messageTemplateUpdate.getTemplateStructure());
        messageTemplate.setActionsCode(messageTemplateUpdate.getActionsCode() == null ? messageTemplate.getActionsCode() : messageTemplateUpdate.getActionsCode());
        messageTemplate.setReturnMessage(messageTemplateUpdate.getReturnMessage() == null ? messageTemplate.getReturnMessage() : messageTemplateUpdate.getReturnMessage());
        messageTemplate.setSubject(messageTemplateUpdate.getSubject() == null ? messageTemplate.getSubject() : messageTemplateUpdate.getSubject());
        messageTemplate.setHeader(messageTemplateUpdate.getHeader() == null ? messageTemplate.getHeader() : messageTemplateUpdate.getHeader());
        messageTemplate.setFooter(messageTemplateUpdate.getFooter() == null ? messageTemplate.getFooter() : messageTemplateUpdate.getFooter());
        return messageTemplate;
    }

    public static List<MessageTemplate> toMessageTemplates(List<MessageTemplateDTO> messageTemplateDTOList) {
        if (messageTemplateDTOList == null) {
            return null;
        }
        return messageTemplateDTOList.stream().map(cr -> toMessageTemplate(cr)).collect(Collectors.toList());
    }

    public static List<MessageTemplateDTO> toMessageTemplateDTOs(List<MessageTemplate> messageTemplateList) {
        if (messageTemplateList == null) {
            return null;
        }
        return messageTemplateList.stream().map(cr -> toMessageTemplateDTO(cr)).collect(Collectors.toList());
    }
}
