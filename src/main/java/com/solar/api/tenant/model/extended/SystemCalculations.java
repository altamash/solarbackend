package com.solar.api.tenant.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "system_calculations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCalculations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long acctId;
    private String refType;
    private Long refId;
    private Long siteId;
    private String calcType;
    private Double calcValue;
    private Date date;
    private String ext1;
    private String ext2;
    private String ext3;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
