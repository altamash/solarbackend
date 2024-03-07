package com.solar.api.tenant.model.workflow;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_placeholder")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePlaceholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String placeholderName;
    @Column(length = 50)
    private String module;
    @Column(length = 100)
    private String description;
    private Boolean multiline;
    private Integer max;
    @Column(length = 50)
    private String alias;
    private Boolean optional;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
