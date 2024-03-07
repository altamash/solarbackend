package com.solar.api.tenant.model.extended.resources;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "resource_assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAssignments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long empId;
    private String assignedTo;
    private Long assignmentRefId;
    private Long roleId; //watcher,contributor,manager

}
