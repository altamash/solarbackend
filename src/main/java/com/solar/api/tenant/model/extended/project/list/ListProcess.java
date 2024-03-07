package com.solar.api.tenant.model.extended.project.list;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "list_process")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long listId;
    private String listName;
    private String category;//assetlist,checklist,contract
    private String subCategory;
    private Boolean enabled;
    private String notes;
    private String assignedTo;
    private Long roleId;
    private String rolesW;
}
