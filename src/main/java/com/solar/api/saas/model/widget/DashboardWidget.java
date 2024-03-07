package com.solar.api.saas.model.widget;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_widget")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "widget_type_id", nullable = false)
    private DashboardWidgetType widgetType;
}
