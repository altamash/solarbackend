package com.solar.api.tenant.model.workflow;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_template_placeholder")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplatePlaceholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long msgTmpId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "placeholder_id", referencedColumnName = "id")
    private MessagePlaceholder messagePlaceholder;
    private Long iterationNum;
    @Column(length = 20)
    private String defaultMsgText; // TODO: Recheck type and/or size
    private Boolean optional;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
