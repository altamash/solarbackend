package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public interface UserSpecification {

    static Specification<User> textInAllColumns(String text) {

        if (!text.contains("%")) {
            text = "%" + text + "%";
        }
        final String finalText = text;

        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                root.fetch("roles", JoinType.LEFT);
                return criteriaBuilder.or(root.getModel().getDeclaredSingularAttributes().stream().filter(a -> {
                            if (a.getJavaType().getSimpleName().equalsIgnoreCase("string")) {
                                return true;
                            } else {
                                return false;
                            }
                        }).map(a -> criteriaBuilder.like(root.get(a.getName()), finalText)
                              ).toArray(Predicate[]::new)
                                         );
            }
        };
    }

    static Specification<User> withFieldValue(String field, Object value) {

        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                root.fetch("roles", JoinType.LEFT);
                return criteriaBuilder.equal(root.get(field), value);
            }
        };
    }

    static Specification<User> withFieldValueNot(String field, Object value) {

        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(root.get(field), value);
            }
        };
    }

}
