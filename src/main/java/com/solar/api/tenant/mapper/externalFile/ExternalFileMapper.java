package com.solar.api.tenant.mapper.externalFile;

import com.solar.api.tenant.model.externalFile.ExternalFile;

import java.util.List;
import java.util.stream.Collectors;

public class ExternalFileMapper {

    /**
     * @param externalFileDTO
     * @return
     */
    public static ExternalFile toExternalFile(ExternalFileDTO externalFileDTO) {
        return ExternalFile.builder()
                .importTypeId(externalFileDTO.getImportTypeId())
                .name(externalFileDTO.getName())
                .importType(externalFileDTO.getImportType())
                .sourceFormat(externalFileDTO.getSourceFormat())
                .wordSeparator(externalFileDTO.getWordSeparator())
                .lineSeparator(externalFileDTO.getLineSeparator())
                .targetTable(externalFileDTO.getTargetTable())
                .eofIdentifier(externalFileDTO.getEofIdentifier())
                .header(externalFileDTO.getHeader())
                .associatedParser(externalFileDTO.getAssociatedParser())
                .build();
    }

    /**
     * @param externalFile
     * @return
     */
    public static ExternalFileDTO toExternalFileDTO(ExternalFile externalFile) {
        if (externalFile == null) {
            return null;
        }

        return ExternalFileDTO.builder()
                .importTypeId(externalFile.getImportTypeId())
                .name(externalFile.getName())
                .importType(externalFile.getImportType())
                .sourceFormat(externalFile.getSourceFormat())
                .wordSeparator(externalFile.getWordSeparator())
                .lineSeparator(externalFile.getLineSeparator())
                .targetTable(externalFile.getTargetTable())
                .eofIdentifier(externalFile.getEofIdentifier())
                .header(externalFile.getHeader())
                .associatedParser(externalFile.getAssociatedParser())
                .build();
    }

    /**
     * @param externalFile
     * @param externalFileUpdate
     * @return
     */
    public static ExternalFile toUpdateExternalFile(ExternalFile externalFile, ExternalFile externalFileUpdate) {
        externalFile.setName(externalFileUpdate.getName() == null ? externalFile.getName() :
                externalFileUpdate.getName());
        externalFile.setImportType(externalFileUpdate.getImportType() == null ? externalFile.getImportType() :
                externalFileUpdate.getImportType());
        externalFile.setSourceFormat(externalFileUpdate.getSourceFormat() == null ? externalFile.getSourceFormat() :
                externalFileUpdate.getSourceFormat());
        externalFile.setWordSeparator(externalFileUpdate.getWordSeparator() == null ?
                externalFile.getWordSeparator() : externalFileUpdate.getWordSeparator());
        externalFile.setLineSeparator(externalFileUpdate.getLineSeparator() == null ?
                externalFile.getLineSeparator() : externalFileUpdate.getLineSeparator());
        externalFile.setTargetTable(externalFileUpdate.getTargetTable() == null ? externalFile.getTargetTable() :
                externalFileUpdate.getTargetTable());
        externalFile.setEofIdentifier(externalFileUpdate.getEofIdentifier() == null ?
                externalFile.getEofIdentifier() : externalFileUpdate.getEofIdentifier());
        externalFile.setHeader(externalFileUpdate.getHeader() == null ? externalFile.getHeader() :
                externalFileUpdate.getHeader());
        externalFile.setAssociatedParser(externalFileUpdate.getAssociatedParser() == null ?
                externalFile.getAssociatedParser() : externalFileUpdate.getAssociatedParser());
        return externalFile;
    }

    /**
     * @param externalFileDTOs
     * @return
     */
    public static List<ExternalFile> toExternalFiles(List<ExternalFileDTO> externalFileDTOs) {
        return externalFileDTOs.stream().map(ef -> toExternalFile(ef)).collect(Collectors.toList());
    }

    /**
     * @param externalFiles
     * @return
     */
    public static List<ExternalFileDTO> toExternalFileDTOs(List<ExternalFile> externalFiles) {
        return externalFiles.stream().map(ef -> toExternalFileDTO(ef)).collect(Collectors.toList());
    }

}
