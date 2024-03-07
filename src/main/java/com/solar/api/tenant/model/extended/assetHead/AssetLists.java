package com.solar.api.tenant.model.extended.assetHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetLists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long listId;
    private String assetListAlias;
    private Long assetId;
    private String scanCode;
    private String preferredSourcingLoc;
    private String visibilityCode; // Not used

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
