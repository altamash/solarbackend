package com.solar.api.tenant.model.widgetconfiguration;

import javax.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "module_widget")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWidget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String widgetName;
    private String widgetSize;
    private String widgetColor;
    private String widgetIcon;
    private String widgetUri;
    @OneToMany(mappedBy = "moduleWidget", cascade = CascadeType.MERGE)
    private List<UserWidget> userWidgets;

}
