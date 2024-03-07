package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserAccount(User userAccount);

    Address findByUserAccountAndAlias(User userAccount, String alias);

    @Query("select a from CustomerSubscriptionMapping csm, Address a " +
            "  where csm.value = a.alias and csm.rateCode=:siteCode " +
            "  and  csm.subscription.id=:customerSubscriptionId and a.userAccount.acctId=:accountId")
    List<Address> findSiteAddressWithAlias(@Param("siteCode") String siteCode,
                                     @Param("customerSubscriptionId") Long customerSubscriptionId,
                                     @Param("accountId") Long accountId);

}
