package com.solar.api.tenant.model.extended.assetHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "scan_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scanId;
    @Column(length = 10)
    private String regCode;
    private Long ref;
    private String scanCode;
    private String codeType;
    private String standardCodeFormat;
    private String status;
    private Boolean temporary;
    private Date startDate;
    private Date expiry;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
