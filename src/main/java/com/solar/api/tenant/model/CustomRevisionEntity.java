package com.solar.api.tenant.model;

import com.solar.api.helper.CustomRevisionEntityListener;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "revinfo")
@Getter
@Setter
@RevisionEntity(CustomRevisionEntityListener.class)
public class CustomRevisionEntity
        extends DefaultRevisionEntity {

    private static final long serialVersionUId = 1L;

    private String userName;
    private String fullName;
    private String clientIp;
}
