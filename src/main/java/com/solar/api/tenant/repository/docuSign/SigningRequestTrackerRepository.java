package com.solar.api.tenant.repository.docuSign;

import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SigningRequestTrackerRepository extends JpaRepository<SigningRequestTracker, Long> {

    Optional<SigningRequestTracker> findByExtRequestId(String extRequestId);
}
