package com.solar.api.saas.model.tenant;

import com.solar.api.saas.model.permission.component.Modules;
import com.solar.api.saas.model.permission.component.SubModule;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_module_access")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantModuleAccess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private MasterTenant masterTenant;

    @OneToOne
    @JoinColumn(name = "module_id")
    private Modules modules;

    @OneToOne
    @JoinColumn(name = "sub_module_id")
    private SubModule subModule;

    private boolean enabled;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
