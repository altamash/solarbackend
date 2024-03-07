package com.solar.api.saas.model.permission.navigation;

import java.util.List;

public class MenuItem {
    private Long navMapId;
    private String displayName;
    private String navUri;
    private Long icon;
    private List<MenuItem> menuItems;
    private List<MenuItem> favourites;
}
