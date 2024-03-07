package com.solar.api.saas.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "image_library")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String alias;
    @Column(length = 10)
    private String category;
    @Column(length = 15)
    private String format;
    private Integer length;
    private Integer width;
    private Integer size;
    private String imageUri;
    private Integer seqNumber;
    @Column(length = 50)
    private String encoding;
    @Column(length = 50)
    private String checksum;
    @Column(length = 50)
    private String altText;
    @Column(length = 50)
    private String keywords;
    @Column(length = 50)
    private String refType; // Eg. Nav
    private Long refId;  // Navigation id

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
