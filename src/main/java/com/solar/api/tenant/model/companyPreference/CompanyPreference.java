package com.solar.api.tenant.model.companyPreference;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "company_preference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, unique = true)
    private String companyCode;
    private Long companyKey;
    private Long locId;
    private String companyName;
    private String logo;
    private Integer emergencySupportNumber;
    private String countryCode;
    private String country;
    private String helpline247;
    private String defaultCurrency;
    private Integer rounding;
    @Column(name = "banners")
    @OneToMany(mappedBy = "companyPreference", cascade = CascadeType.MERGE)
    private List<Banner> banners;
    private String pictureRollingDelay;
    private String facebookURL;
    private String twitterURL;
    private String youtubeURL;
    private String linkedInURL;
    private String faqURL;
    private String websiteURL;
    private String companyPolicy;
    private String emailAddress;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String adminLanding;
    @Column(name = "landing_text")
    private String landingText;
    @Column(name = "landing_images_url")
    private String landingImagesUrl;
    @Column(name = "mobile_landing_images_url")
    private String mobileLandingImagesUrl;

    @Column(length = 500)
    private String cpConfigure;
    private String companyLanding;
    @Column(name = "admin_welcome_widget_text")
    private String adminWelcomeWidgetText;
    @Column(name = "customer_welcome_widget_text")
    private String customerWelcomeWidgetText;
    private String companyTerms;
    @Column(name = "landing_description")
    private String landingDescription;

}
