package com.solar.api.saas.service.process.upload.v2.customer.mapper;

import com.solar.api.helper.Utility;
import com.solar.api.helper.ValidationUtils;
import com.solar.api.saas.service.process.upload.v2.mapper.Customer;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public static User toUser(Map m) throws ParseException {
        if (m == null) {
            return null;
        }
        // Fields from com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2
        // Lead Type	First Name	Last Name	Phone Number	Email	Pirmary Zip Code	Utility Provider
        // Location Name	Organization	Location Type	Address 1	Address 2	City	State	Country	Zip Code
        // Account Holder Name 	Utility Provide	Premise Number 	Reference Number / ID	Average Monthly Bill
        // Payment Method	Account Title	Bank 	Account Type 	Soft Credit Check

        // First Name, Last Name, Email
        return User.builder()
                .firstName(m.get("Lead Type") != null ? (String) m.get("Lead Type") : null)
                .firstName(m.get("First Name") != null ? (String) m.get("First Name") : null)
                .lastName(m.get("Last Name") != null ? (String) m.get("Last Name") : null).build();
    }

    public static User toUser(CustomerV2 customerV2, UserType userType, Long maxAcctId) {
        if (customerV2 == null) {
            return null;
        }
        // Fields from com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2
        // Lead Type	First Name	Last Name	Phone Number	Email	Pirmary Zip Code	Utility Provider
        // Location Name	Organization	Location Type	Address 1	Address 2	City	State	Country	Zip Code
        // Account Holder Name 	Utility Provide	Premise Number 	Reference Number / ID	Average Monthly Bill
        // Payment Method	Account Title	Bank 	Account Type 	Soft Credit Check

        return User.builder()
                .firstName(customerV2.getFirstName())
                .lastName(customerV2.getLastName())
                .emailAddress(customerV2.getEmail())
                .userType(userType)
                .userName(customerV2.getEmail()+maxAcctId)
                .build();
        // First Name, Last Name, Email
    }

    public static List<User> toUsersV2(List<Map> mappings) {
        return mappings.stream().map(m -> {
            try {
                return toUser(m);
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
