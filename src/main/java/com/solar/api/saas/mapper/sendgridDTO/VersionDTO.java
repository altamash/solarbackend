package com.solar.api.saas.mapper.sendgridDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionDTO {

    private String id;
    private String user_id;
    private String template_id;
    private String active;
    private String name;
    private String html_content;
    private String plain_content;
    private Boolean generate_plain_content;
    private String subject;
    private String updated_at;
    private String editor;
    private String thumbnail_url;

}
