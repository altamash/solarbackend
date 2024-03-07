package com.solar.api.tenant.mapper.extended.document;

import com.solar.api.tenant.model.extended.document.DocuHistory;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.helper.Utility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocuMapper {

    // DocuLibrary ////////////////////////////////////////////////
    public static DocuLibrary toDocuLibrary(DocuLibraryDTO docuLibraryDTO) {
        if (docuLibraryDTO == null) {
            return null;
        }
        return DocuLibrary.builder()
                .docuId(docuLibraryDTO.getDocuId())
                .docuType(docuLibraryDTO.getDocuType())
                .codeRefType(docuLibraryDTO.getCodeRefType())
                .codeRefId(docuLibraryDTO.getCodeRefId())
                .docuName(docuLibraryDTO.getDocuName())
                .notes(docuLibraryDTO.getNotes())
                .tags(docuLibraryDTO.getTags())
                .format(docuLibraryDTO.getFormat())
                .uri(docuLibraryDTO.getUri())
                .status(docuLibraryDTO.getStatus())
                .visibilityKey(docuLibraryDTO.getVisibilityKey())
                .compKey(docuLibraryDTO.getCompKey())
                .resourceInterval(docuLibraryDTO.getResourceInterval())
                .build();
    }

    public static DocuLibraryDTO toDocuLibraryDTO(DocuLibrary docuLibrary) {
        if (docuLibrary == null) {
            return null;
        }
        return DocuLibraryDTO.builder()
                .docuId(docuLibrary.getDocuId())
                .docuType(docuLibrary.getDocuType())
                .codeRefType(docuLibrary.getCodeRefType())
                .codeRefId(docuLibrary.getCodeRefId())
                .docuName(docuLibrary.getDocuName())
                .notes(docuLibrary.getNotes())
                .tags(docuLibrary.getTags())
                .format(docuLibrary.getFormat())
                .uri(docuLibrary.getUri())
                .status(docuLibrary.getStatus())
                .visibilityKey(docuLibrary.getVisibilityKey())
                .compKey(docuLibrary.getCompKey())
                .resourceInterval(docuLibrary.getResourceInterval())
                .createdAt(docuLibrary.getCreatedAt())
                .updatedAt(docuLibrary.getUpdatedAt())
                .build();
    }

    public static DocumentDTO toDocumentDTO(DocuLibrary docuLibrary) {
        if (docuLibrary == null) {
            return null;
        }
        return DocumentDTO.builder()
                .id(docuLibrary.getDocuId())
                .type(docuLibrary.getDocuType())
                .name(docuLibrary.getDocuName())
                .size(docuLibrary.getSize())
                .file(docuLibrary.getUri())
                .updatedAt(docuLibrary.getUpdatedAt())
                .format(docuLibrary.getFormat())
                .build();
    }
    public static DocumentDTO toDocumentDTORounding(DocuLibrary docuLibrary,Integer rounding) {
        if (docuLibrary == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)").matcher(docuLibrary.getSize());
        String num = matcher.find() ? matcher.group(1) : "0";
        return DocumentDTO.builder()
                .id(docuLibrary.getDocuId())
                .type(docuLibrary.getDocuType())
                .name(docuLibrary.getDocuName())
                .size(Utility.rounding(num,rounding) + " MB")
                .file(docuLibrary.getUri())
                .updatedAt(docuLibrary.getUpdatedAt())
                .format(docuLibrary.getFormat())
                .build();
    }

    public static DocuLibrary toUpdatedDocuLibrary(DocuLibrary docuLibrary, DocuLibrary docuLibraryUpdate) {
        docuLibrary.setDocuType(docuLibraryUpdate.getDocuType() == null ? docuLibrary.getDocuType() :
                docuLibraryUpdate.getDocuType());
        docuLibrary.setCodeRefType(docuLibraryUpdate.getCodeRefType() == null ? docuLibrary.getCodeRefType() :
                docuLibraryUpdate.getCodeRefType());
        docuLibrary.setCodeRefId(docuLibraryUpdate.getCodeRefId() == null ? docuLibrary.getCodeRefId() :
                docuLibraryUpdate.getCodeRefId());
        docuLibrary.setDocuName(docuLibraryUpdate.getDocuName() == null ? docuLibrary.getDocuName() :
                docuLibraryUpdate.getDocuName());
        docuLibrary.setNotes(docuLibraryUpdate.getNotes() == null ? docuLibrary.getNotes() :
                docuLibraryUpdate.getNotes());
        docuLibrary.setTags(docuLibraryUpdate.getTags() == null ? docuLibrary.getTags() : docuLibraryUpdate.getTags());
        docuLibrary.setFormat(docuLibraryUpdate.getFormat() == null ? docuLibrary.getFormat() :
                docuLibraryUpdate.getFormat());
        docuLibrary.setUri(docuLibraryUpdate.getUri() == null ? docuLibrary.getUri() : docuLibraryUpdate.getUri());
        docuLibrary.setStatus(docuLibraryUpdate.getStatus() == null ? docuLibrary.getStatus() :
                docuLibraryUpdate.getStatus());
        docuLibrary.setVisibilityKey(!docuLibraryUpdate.getVisibilityKey() ? docuLibrary.getVisibilityKey() :
                docuLibraryUpdate.getVisibilityKey());
        docuLibrary.setCompKey(docuLibraryUpdate.getCompKey() == null ? docuLibrary.getCompKey() : docuLibraryUpdate.getCompKey());
        docuLibrary.setResourceInterval(docuLibraryUpdate.getResourceInterval() == null ? docuLibrary.getResourceInterval() : docuLibraryUpdate.getResourceInterval());
        return docuLibrary;
    }

    public static List<DocuLibrary> toDocuLibrarys(List<DocuLibraryDTO> docuLibraryDTOS) {
        return docuLibraryDTOS.stream().map(d -> toDocuLibrary(d)).collect(Collectors.toList());
    }

    public static List<DocuLibraryDTO> toDocuLibraryDTOs(List<DocuLibrary> docuLibraries) {
        return docuLibraries.stream().map(d -> toDocuLibraryDTO(d)).collect(Collectors.toList());
    }

    public static List<DocumentDTO> toDocumentDTOs(List<DocuLibrary> docuLibraries) {
        return docuLibraries.stream().map(d -> toDocumentDTO(d)).collect(Collectors.toList());
    }
    public static List<DocumentDTO> toDocumentDTOsRounding(List<DocuLibrary> docuLibraries,Integer rounding) {
        return docuLibraries.stream().map(d -> toDocumentDTORounding(d,rounding)).collect(Collectors.toList());
    }


    // DocuHistory ////////////////////////////////////////////////
    public static DocuHistory toDocuHistory(DocuHistoryDTO docuHistoryDTO) {
        if (docuHistoryDTO == null) {
            return null;
        }
        return DocuHistory.builder()
                .id(docuHistoryDTO.getId())
                .docuId(docuHistoryDTO.getDocuId())
                .version(docuHistoryDTO.getVersion())
                .uri(docuHistoryDTO.getUri())
                .createUpdateDatetime(docuHistoryDTO.getCreateUpdateDatetime())
                .updatedBy(docuHistoryDTO.getUpdatedBy())
                .build();
    }

    public static DocuHistoryDTO toDocuHistoryDTO(DocuHistory docuHistory) {
        if (docuHistory == null) {
            return null;
        }
        return DocuHistoryDTO.builder()
                .id(docuHistory.getId())
                .docuId(docuHistory.getDocuId())
                .version(docuHistory.getVersion())
                .uri(docuHistory.getUri())
                .createUpdateDatetime(docuHistory.getCreateUpdateDatetime())
                .updatedBy(docuHistory.getUpdatedBy())
                .createdAt(docuHistory.getCreatedAt())
                .updatedAt(docuHistory.getUpdatedAt())
                .build();
    }

    public static DocuHistory toUpdatedDocuHistory(DocuHistory docuHistory, DocuHistory docuHistoryUpdate) {
        docuHistory.setDocuId(docuHistoryUpdate.getDocuId() == null ? docuHistory.getDocuId() :
                docuHistoryUpdate.getDocuId());
        docuHistory.setVersion(docuHistoryUpdate.getVersion() == null ? docuHistory.getVersion() :
                docuHistoryUpdate.getVersion());
        docuHistory.setUri(docuHistoryUpdate.getUri() == null ? docuHistory.getUri() : docuHistoryUpdate.getUri());
        docuHistory.setCreateUpdateDatetime(docuHistoryUpdate.getCreateUpdateDatetime() == null ?
                docuHistory.getCreateUpdateDatetime() : docuHistoryUpdate.getCreateUpdateDatetime());
        docuHistory.setUpdatedBy(docuHistoryUpdate.getUpdatedBy() == null ? docuHistory.getUpdatedBy() :
                docuHistoryUpdate.getUpdatedBy());

        return docuHistory;
    }

    public static List<DocuHistory> toDocuHistorys(List<DocuHistoryDTO> docuHistoryDTOS) {
        return docuHistoryDTOS.stream().map(d -> toDocuHistory(d)).collect(Collectors.toList());
    }

    public static List<DocuHistoryDTO> toDocuHistoryDTOs(List<DocuHistory> docuHistories) {
        return docuHistories.stream().map(d -> toDocuHistoryDTO(d)).collect(Collectors.toList());
    }

    public static List<DocumentDTO> applyRoundingToDocumentDTOList(List<DocumentDTO> documentDTOList, Integer rounding) {
        if (documentDTOList == null || documentDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        return documentDTOList.stream().map(documentDTO -> {
            Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)").matcher(documentDTO.getSize());
            if (matcher.find()) {
                String num = matcher.group(1);
                String roundedSize = Utility.rounding(num, rounding) + " MB";
                documentDTO.setSize(roundedSize);
            }
            return documentDTO;
        }).collect(Collectors.toList());
    }
}
