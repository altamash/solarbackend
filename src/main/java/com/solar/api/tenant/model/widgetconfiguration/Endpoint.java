package com.solar.api.tenant.model.widgetconfiguration;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "endpoint")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String url;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.MERGE)
    private List<UserWidget> userWidgets;

}
