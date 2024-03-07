package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhysicalLocationDTO {

    private Long id;
    private String locationName;
    private String locationType;
    private String otherDetails;
    private String add1;
    private String add2;
    private String add3;
    private String contactPerson;
    private String phone;
    private String category; //ext;
    private String email;
    //private String externalRefId;
    private Long externalRefId;
    private String geoLat;
    private String geoLong;
    private String googleCoordinates;
    private String terrainType;
    private String status;
    private String active;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private Boolean correspondenceAddress;
    private Boolean primaryIndex;
    private String zipCode;
    private Long entityId;
    private Boolean isPrimary;

    private Boolean isDeleted;

    private Long orgId;
    private String orgName;
    private List<SiteDTO> siteDTOs;
    private OrganizationDTO organizationDTO;
    private Long acctId;
    private String isPrimaryInd;
    private Boolean isAvailable;

    @Override
    public String toString() {
        return "PhysicalLocationDTO{" +
                "id=" + id +
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
                ", geoLat='" + geoLat + '\'' +
                ", geoLong='" + geoLong + '\'' +
                ", googleCoordinates='" + googleCoordinates + '\'' +
                ", terrainType='" + terrainType + '\'' +
                ", status='" + status + '\'' +
                ", active='" + active + '\'' +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", ext3='" + ext3 + '\'' +
                ", ext4='" + ext4 + '\'' +
                ", correspondenceAddress=" + correspondenceAddress +
                ", primaryIndex=" + primaryIndex +
                ", isPrimary=" + isPrimary +
                ", orgId=" + orgId +
                ", orgName=" + orgName +
                '}';
    }

    public PhysicalLocationDTO(Long id, String add1, String add2, String zipCode, String geoLat, String geoLong, String ext1, String ext2, String contactPerson, String email) {
        this.id = id;
        this.add1 = add1;
        this.add2 = add2;
        this.zipCode = zipCode;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.contactPerson = contactPerson;
        this.email = email;

    }

    public PhysicalLocationDTO(Long id, String add1, String add2, String zipCode, String geoLat, String geoLong, String ext1, String ext2, String contactPerson, String email, String locationName
            , String locationType) {
        this.id = id;
        this.add1 = add1;
        this.add2 = add2;
        this.zipCode = zipCode;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.contactPerson = contactPerson;
        this.email = email;
        this.locationName = locationName;
        this.locationType = locationType;

    }

    public PhysicalLocationDTO(Long id, String locationName, String otherDetails, String add1, String add2,
                               String add3, String geoLat, String geoLong, String googleCoordinates, String ext1,
                               String ext2, String ext3, String ext4, String zipCode,Boolean isAvailable) {
        this.id = id;
        this.locationName = locationName;
        this.otherDetails = otherDetails;
        this.add1 = add1;
        this.add2 = add2;
        this.add3 = add3;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
        this.googleCoordinates = googleCoordinates;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.ext4 = ext4;
        this.zipCode = zipCode;
        this.isAvailable = isAvailable;
    }

    public PhysicalLocationDTO(Long id, String add1, String add2, String ext1, String ext2, String zipCode) {
        this.id = id;
        this.add1 = add1;
        this.add2 = add2;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.zipCode = zipCode;
    }
}
