package com.solar.api.tenant.mapper.tiles.customersupportmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSupportTemplateTile {
    private Long Id;


    private String Summary;


    private String Message;


    private String Category;


    private String SubCategory;


    private String Priority;


    private String SourceId;


    private String Status;


    private Long RaisedBy;


    private String FirstName;


    private String LastName;


    private String Role;


    private String FormattedCreatedAt;


    private String RaisedByImgUri;


    private Long AssigneeEntityRoleId;


    private String AssigneeEntityName;


    private String AssigneeImgUri;


    private String RequesterName;


    private String RequesterImgUri;


    private String RequesterEmail;


    private String RequesterPhone;


    private String RequesterType;


    private String SourceType;


    private String VariantId;


    private String GardenName;


    private String SubscriptionId;


    private String SubscriptionName;


    private String GardenImgUri;
    private String groupBy;

}
