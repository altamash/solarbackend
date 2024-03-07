package com.solar.api.tenant.model.widgetconfiguration;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_widget")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWidget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String widgetName;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_widget_id")
    private ModuleWidget moduleWidget;
    @JoinColumn(name = "endpoint_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Endpoint endpoint;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "acct_id")
    private Long acctId;


}
