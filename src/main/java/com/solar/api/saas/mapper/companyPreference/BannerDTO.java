package com.solar.api.saas.mapper.companyPreference;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerDTO {

    /**
     * Refences JSON object
     */
    //    [
    //      {
    //        image:
    //        'banner0-9.jpg',
    //         picseq:1,
    //         URL:'www.google.com'
    //      }
    //  ];

    private Long id;
    private Long companyPreferenceId;
    private String image;
    private Integer pictureSequence;
    private String url;
    private String filename;
    private Integer idx;
    private String redirectUrl;
    private String imageUrl;
}
