package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerMapper.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

    public static User toUser(Customer customer) throws ParseException {
        if (customer == null) {
            return null;
        }
        return User.builder()
                .externalId(customer.getExternalId())
                .compKey(!customer.getCompKey().isEmpty() ? Long.parseLong(customer.getCompKey()) : null)
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .userName(customer.getUser())
                .password(customer.getPassword())
                .IdCode(customer.getIdCode())
                .authorityId(customer.getAuthorityId())
                .gender(customer.getGender())
                .dataOfBirth(customer.getDataOfBirth() != null && !customer.getDataOfBirth().isEmpty() ?
                        dateFormat.parse(customer.getDataOfBirth()) : null)
                .registerDate(customer.getRegisterDate() != null && !customer.getRegisterDate().isEmpty() ?
                        dateFormat.parse(customer.getRegisterDate()) : null)
                .activeDate(customer.getActiveDate() != null && !customer.getActiveDate().isEmpty() ?
                        dateFormat.parse(customer.getActiveDate()) : null)
                .status(customer.getStatus())
                .prospectStatus(customer.getProspectStatus())
                .referralEmail(customer.getReferralEmail())
                .deferredContactDate(customer.getDeferredContactDate() != null && !customer.getDeferredContactDate().isEmpty() ? dateFormat.parse(customer.getDeferredContactDate()) : null)
                .language(customer.getLanguage())
                .authentication(customer.getAuthentication())
                .category(customer.getCategory())
                .groupId(customer.getGroupId())
                .socialUrl(customer.getSocialUrl())
                .emailAddress(customer.getEmail())
                .build();
    }

    public static List<User> toUsers(List<Customer> customers) {
        return customers.stream().map(c -> {
            try {
                return toUser(c);
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
