package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root<T> implements Serializable {
    public Content content;

     public static class Content{
        public ArrayList<WorkOrder> workOrder;
        public ArrayList<Content> content;
     }

     public static class WorkOrder{
        public String name;
        public Long ticket_number;
        public String support_ticket_generated_flag;
        public String subscription_id;
    }

}
