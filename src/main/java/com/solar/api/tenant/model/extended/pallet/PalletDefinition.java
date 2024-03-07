package com.solar.api.tenant.model.extended.pallet;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pallet_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long palletRefId;
    private Long palletTypeId;
    private String source;
    private Long sourceRefId;
    private String returnToSourceInd;
    private String status;
    private String returnDatetime;
    private String inspectedBy;
    private String inspectionDatetime;
    private String lockedInd;
    private String lastLocSeqNo;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
