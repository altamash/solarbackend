package com.solar.api.tenant.model.permission.navigation;

import com.solar.api.tenant.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "nav_user_map")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationUserMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long navMapId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acct_id", referencedColumnName = "acctId")
    private User user;
    @Column(name = "active_nav_ind")
    private String activeNavIndicator;
    @Column(name = "fav_ind")
    private Boolean favIndicator;
    private Long icoUri;
}
