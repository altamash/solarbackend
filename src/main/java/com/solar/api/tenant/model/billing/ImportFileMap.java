package com.solar.api.tenant.model.billing;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "import_file_map")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportFileMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long importTypeId;
    private String rateCode;
    private String headerColumnName;
    private Integer startPosition;
    private Integer endPosition;
    private String lineHead;
}
