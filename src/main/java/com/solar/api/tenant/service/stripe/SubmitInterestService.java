package com.solar.api.tenant.service.stripe;

import com.solar.api.tenant.mapper.stripe.SubmitInterestDTO;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SubmitInterestService {

    ResponseEntity<Object> saveSubmitInterest(SubmitInterestDTO submitInterestDTO) throws IOException;


}
