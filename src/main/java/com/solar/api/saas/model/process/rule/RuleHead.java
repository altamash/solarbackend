package com.solar.api.saas.model.process.rule;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rule_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ruleId;
    private String ruleDescription;
    private String subscriptionCode;
    private String method;
    private String billingCode;
    private String formula;
    private String components;
    private String ruleDependency;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
