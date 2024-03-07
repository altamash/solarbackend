package com.solar.api.tenant.model.extended.document;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "docu_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocuHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long docuId;
    private Float version;
    private String uri;
    private Date createUpdateDatetime;
    private Long updatedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
