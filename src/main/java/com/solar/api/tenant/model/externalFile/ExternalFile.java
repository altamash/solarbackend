package com.solar.api.tenant.model.externalFile;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "external_file_mapper")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long importTypeId;
    private String name;
    private String importType;
    private String sourceFormat;
    private String wordSeparator;
    private String lineSeparator;
    private String targetTable;
    private String eofIdentifier;
    private String header;
    private String associatedParser;
    private String params;
}
