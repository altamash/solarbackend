package com.solar.api.saas.service.workflow.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class TypeBListJson {

/*
    {
        "wlist": {
            "Type": "B",
            "recipients": {
                "e": ["G1231", "1242", "1255", "5334", "2512"],
                "n": ["1232", "1251", "1245", "5234", "3512"]
            }
        }
    }
*/
    WListJson wlist;
}
