package com.solar.api.tenant.model.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp_pass")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempPass {

    @Id
    @Column(name = "acct_id")
    private Long acctId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "clear_pass")
    private String clearPass;
    @Column(name = "enc_pass")
    private String encPass;

}
