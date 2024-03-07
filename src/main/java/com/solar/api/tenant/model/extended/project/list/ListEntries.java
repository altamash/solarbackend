package com.solar.api.tenant.model.extended.project.list;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "list_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListEntries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long listId;
    private String title;
    private String description;
    private Long sequence;
    private Boolean mandatory;
    private Boolean enabled;
    private String type;
    private Long docuId;
    private String assignedTo;

}
