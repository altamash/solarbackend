package com.solar.api.tenant.mapper.tiles.weatherTile;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor

public class GardenDetail {

    private String refId;
    private String geoLatitude;
    private String geoLongitude;

    public GardenDetail(String refId, String geoLatitude, String geoLongitude) {
        this.refId = refId;
        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;
    }
}
