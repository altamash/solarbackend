package com.solar.api.saas.service.process.upload.mapper;

import com.solar.api.helper.Utility;
import com.solar.api.helper.ValidationUtils;
import com.solar.api.tenant.model.user.User;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomerMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerMapper.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

    public static User toUser(Customer customer) throws ParseException {
        if (customer == null) {
            return null;
        }
        User.UserBuilder userBuilder = User.builder()
//                .externalId(customer.getExternalId())
//                .compKey(!customer.getCompKey().isEmpty() ? Long.parseLong(customer.getCompKey()) : null)
                .action(customer.getAction())
                .acctId(customer.getAcctId() != null && customer.getAcctId().equals("") ? null : customer.getAcctId())
                .firstName(customer.getFirstName() != null && customer.getFirstName().equals("") ? null :
                        customer.getFirstName())
                .lastName(customer.getLastName() != null && customer.getLastName().equals("") ? null :
                        customer.getLastName())
                .userName(customer.getUserName() != null && customer.getUserName().equals("") ? null :
                        customer.getUserName())
                .uploadPassword(customer.getPassword() != null && customer.getPassword().equals("") ? null :
                        customer.getPassword())
                .IdCode(customer.getIdCode() != null && customer.getIdCode().equals("") ? null : customer.getIdCode())
                .authorityId(customer.getAuthorityId() != null && customer.getAuthorityId().equals("") ? null :
                        customer.getAuthorityId())
                .gender(customer.getGender() != null && customer.getGender().equals("") ? null : customer.getGender())
                .dataOfBirth(customer.getDataOfBirth() != null && !customer.getDataOfBirth().isEmpty() ?
                        dateFormat.parse(customer.getDataOfBirth()) : null)
                .registerDate(customer.getRegisterDate() != null && !customer.getRegisterDate().isEmpty() ?
                        dateFormat.parse(customer.getRegisterDate()) : null)
                .activeDate(customer.getActiveDate() != null && !customer.getActiveDate().isEmpty() ?
                        dateFormat.parse(customer.getActiveDate()) : null)
                .status(customer.getStatus() != null && customer.getStatus().equals("") ? null : customer.getStatus())
                .prospectStatus(customer.getProspectStatus() != null && customer.getProspectStatus().equals("") ?
                        null : customer.getProspectStatus())
                .referralEmail(customer.getReferralEmail() != null && customer.getReferralEmail().equals("") ? null :
                        customer.getReferralEmail())
                .deferredContactDate(customer.getDeferredContactDate() != null && customer.getDeferredContactDate() != null && !customer.getDeferredContactDate().isEmpty() ? dateFormat.parse(customer.getDeferredContactDate()) : null)
                .language(customer.getLanguage() != null && customer.getLanguage().equals("") ? null :
                        customer.getLanguage())
                .authentication(customer.getAuthentication() != null && customer.getAuthentication().equals("") ?
                        null : customer.getAuthentication())
                .category(customer.getCategory() != null && customer.getCategory().equals("") ? null :
                        customer.getCategory())
                .groupId(customer.getGroupId() != null && customer.getGroupId().equals("") ? null :
                        customer.getGroupId())
                .socialUrl(customer.getSocialUrl() != null && customer.getSocialUrl().equals("") ? null :
                        customer.getSocialUrl())
                .emailAddress(customer.getEmail() != null && customer.getEmail().equals("") ? null :
                        customer.getEmail());
                /*.ccd(customer.getCcd() != null && customer.getCcd().equalsIgnoreCase("YES") ? Boolean.TRUE :
                        Boolean.FALSE)*/
                if (customer.getCcd() != null) {
                    userBuilder.ccd(customer.getCcd().equalsIgnoreCase("YES") ? Boolean.TRUE :
                            Boolean.FALSE);
                }
                return userBuilder.build();
    }

    public static Customer toUserHealth(Customer customer) throws ParseException {
        if (customer == null) {
            return null;
        }
        return Customer.builder()
                .rowNumber(customer.getRowNumber())
                .companyId(customer.getCompanyId() == null ? "'Company id' is required" : null)
                .firstName(customer.getFirstName() == null ? "First name is required" : null)
                .lastName(customer.getLastName())
//                .userName(customer.getUser())
//                .password(customer.getPassword())
//                .IdCode(customer.getIdCode())
//                .authorityId(customer.getAuthorityId())
//                .gender(customer.getGender())
                .dataOfBirth(getDateFormatMsg(customer.getDataOfBirth()))
                .registerDate(getDateFormatMsg(customer.getRegisterDate()))
                .activeDate(getDateFormatMsg(customer.getActiveDate()))
//                .status(customer.getStatus())
//                .prospectStatus(customer.getProspectStatus())
                .email(customer.getEmail() != null && !ValidationUtils.isValidEmail(customer.getEmail()) ? "Invalid email" : null)
                .referralEmail(customer.getReferralEmail() != null && !ValidationUtils.isValidEmail(customer.getReferralEmail()) ? "Invalid referralEmail" : null)
                .deferredContactDate(getDateFormatMsg(customer.getDeferredContactDate()))
                .language(customer.getLanguage() != null && !ValidationUtils.isValidLanguageCode(customer.getLanguage().toLowerCase()) ? "Language is not valid" : null)
//                .authentication(customer.getAuthentication())
//                .category(customer.getCategory())
//                .groupId(customer.getGroupId())
//                .socialUrl(customer.getSocialUrl())
                .build();
    }

    private static String getDateFormatMsg(String date) {
        return date != null && !ValidationUtils.isValidDate(date, Utility.SYSTEM_DATE_FORMAT) ?
                date + " should be " + Utility.SYSTEM_DATE_FORMAT : null;
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

    public static void main(String[] a) {
        String key= "ms-MY";
        Locale locale = new Locale.Builder().setLanguageTag(key).build();
        if (LocaleUtils.isAvailableLocale(new Locale.Builder().setLanguageTag(key).build()))
        {
            System.out.println("Locale present");
        }
    }
}
