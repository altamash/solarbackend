package com.solar.api.tenant.model.user;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "unique_reset_link")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniqueResetLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tenantId;
    private Long userAccount;
    private Long adminAccount;
    private String uniqueText;
    private Boolean usedIndicator;

    @CreationTimestamp
    private LocalDateTime generatedOn;

}
