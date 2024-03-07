package com.solar.api.tenant.model.workflow;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String msgTmplName;
    private String msgTemplRefId;
    @Column(length = 20)
    private String messageType;
    private String parentTmplId; // TODO: Not decided
    @Column(length = 30)
    private String format;
    @Column(length = 5000)
    private String templateHTMLCode;
    @Column(length = 20)
    private String templateStructure;
    @Column(length = 25)
    private String actionsCode;
    private String returnMessage;
    @Column(length = 150)
    private String subject;
    private String header;
    private String footer;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
