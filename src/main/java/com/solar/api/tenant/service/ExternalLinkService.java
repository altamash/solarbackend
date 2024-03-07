package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.solar.api.tenant.mapper.user.ExternalLinkDTO;

import java.io.IOException;
import java.util.Map;

public interface ExternalLinkService {

    String verifyUser(String email, Long compKey, String subject) throws IOException;

    Response sendLink(String email, String url, String salt, Long compKey, String subject) throws IOException;

    ObjectNode tokenVerification(String token);

    ObjectNode resetUserStatus(ExternalLinkDTO externalLinkDTO);

    ObjectNode resetInternalUserStatus(ExternalLinkDTO externalLinkDTO);

    ObjectNode resetCAUserStatus(ExternalLinkDTO externalLinkDTO);

    Map tokenSessionExpiryVerification(String token, Long compKey) ;


}
