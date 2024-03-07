package com.solar.api.saas.model.permission.navigation;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "nav_element")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "nav_id")
    private Long id;
    @Column(length = 50)
    private String navName;
    @Column(length = 100)
    private String displayName;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent", referencedColumnName = "id")
    private NavigationElement parent;
    private Boolean enabled;
    @Column(name = "active_nav_ind")
    private String activeNavIndicator;
    @Column(length = 10)
    private String channel;
    private String navUri;
    private String icoUri;
    @Transient
    private List<NavigationElement> subElements;
}
