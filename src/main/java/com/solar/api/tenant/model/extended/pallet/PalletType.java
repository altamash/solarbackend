package com.solar.api.tenant.model.extended.pallet;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

//@Entity
//@Table(name = "pallet_types")
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class PalletType {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String palletType;
    private String material;
    private String stackable;
    private String division;
    private String prefix; //4 (CHAR)
    private String desc;
    private String fragileAllowed;
    private Long minTemp; //(F)
    private Long maxTemp; //(F)
    private String disposalDocRef;
    private String packagingRef;
    private String storageRecommendations;
    private Long length;
    private Long width;
    private Long height;
    private Long maxWeight;
    private String lastGeneratedSequence;
    private String palletImage;
    private String thumbnail;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
