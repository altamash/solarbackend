package com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDataDTO {

    private String uid;
    private long timestamp;
    private String token;
    private String client;
    private String version;
    private String language;

    @Override
    public String toString(){
        return  "{\"client\":\""+client+"\",\"version\":\""+version+"\","+
                "\"language\":\""+language+"\","+"\"timestamp\":"+timestamp+
                ",\"uid\":\""+uid+"\","+"\"token\":\""+token+"\""+ "}";
    }

}
