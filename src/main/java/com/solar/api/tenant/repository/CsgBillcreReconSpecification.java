package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.CsgBillcreRecon;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CsgBillcreReconSpecification {

    static Specification<CsgBillcreRecon> withFieldValue(String field, Object value) {

        return new Specification<CsgBillcreRecon>() {
            @Override
            public Predicate toPredicate(Root<CsgBillcreRecon> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(field), value);
            }
        };
    }
}
