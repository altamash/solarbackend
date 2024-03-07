package com.solar.api.tenant.service.etl;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.model.workflow.WorkflowGroupAssignment;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "etl_stage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ETLStage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "user_mapping_id")
    private Long userMappingId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
