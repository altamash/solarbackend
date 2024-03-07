package com.solar.api.saas.service.process.upload.v2.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAddress {

    private String action;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("location_id")
    private Long locationId;
    @JsonProperty("location_name")
    private String locationName;
    @JsonProperty("location_type")
    private String locationType;
    @JsonProperty("other_details")
    private String otherDetails;
    @JsonProperty("add1")
    private String add1;
    @JsonProperty("add2")
    private String add2;
    @JsonProperty("add3")
    private String add3;
    @JsonProperty("contact_person")
    private String contactPerson;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("category")
    private String category; //ext;
    @JsonProperty("email")
    private String email;
    //private String externalRefId;
    @JsonProperty("external_ref_id")
    private Long externalRefId;
    @JsonProperty("geo_lat")
    private String geoLat;
    @JsonProperty("geo_long")
    private String geoLong;
    @JsonProperty("google_coordinates")
    private String googleCoordinates;
    @JsonProperty("terrain_type")
    private String terrainType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("active")
    private String active;
    @JsonProperty("ext1")
    private String ext1;
    @JsonProperty("ext2")
    private String ext2;
    @JsonProperty("ext3")
    private String ext3;
    @JsonProperty("ext4")
    private String ext4;
    @JsonProperty("correspondence_address")
    private Boolean correspondenceAddress; //use it for unique measure value
    @JsonProperty("primary_index")
    private Boolean primaryIndex; //use it for primaryIndex
    @JsonProperty("zip_code")
    private String zipCode; //mandatory
    @Override
    public String toString() {
        return "CustomerAddress{" +
                "action='" + action + '\'' +
                ", acctId='" + entityId + '\'' +
                ", locationId='" + locationId + '\'' +
                ", locationName='" + locationName + '\'' +
                ", locationType='" + locationType + '\'' +
                ", otherDetails='" + otherDetails + '\'' +
                ", add1='" + add1 + '\'' +
                ", add2='" + add2 + '\'' +
                ", add3='" + add3 + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phone='" + phone + '\'' +
                ", category='" + category + '\'' +
                ", email='" + email + '\'' +
                ", externalRefId='" + externalRefId + '\'' +
                ", geoLat='" + getGeoLat() + '\'' +
                ", geoLong='" + getGeoLong() + '\'' +
                ", googleCoordinates='" + getGoogleCoordinates() + '\'' +
                ", terraintype='" + terrainType + '\'' +
                ", status='" + status + '\'' +
                ", active='" + active + '\'' +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", ext3='" + ext3 + '\'' +
                ", ext4='" + ext4 + '\'' +
                ", correspondenceAddress='" + correspondenceAddress + '\'' +
                ", primaryIndex='" + primaryIndex + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
