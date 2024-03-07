package com.solar.api.tenant.model;

import com.solar.api.tenant.model.user.userType.EUserType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "alert_calculation_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertCalculationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String valuesJson;
    private String status;
    @Column(length = 30)
    private String gardenId;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum EStatus {
        GENERATED("GENERATED"),
        NOT_GENERATED("NOT_GENERATED");
        String name;

        EStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static EStatus get(String name) {
            return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
        }
    }
}
