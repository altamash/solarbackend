package com.solar.api.tenant.model.companyPreference;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_preference_id")
    private CompanyPreference companyPreference;
    private String image;
    private Integer pictureSequence;
    private String url;
    private Integer idx;
    private String filename;
    private String redirectUrl;
    private String imageUrl;
    @Transient
    private Long companyPreferenceId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
