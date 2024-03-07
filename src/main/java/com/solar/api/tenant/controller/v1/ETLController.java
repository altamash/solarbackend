package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@RestController("ETLController")
@RequestMapping(value = "/etl")
public class ETLController {

    @Autowired
    private ETLService etlService;
    @GetMapping("/processETL")
    public ObjectNode processETL(@RequestHeader("Comp-Key") Long compKey){
        ObjectNode response = new ObjectMapper().createObjectNode();
        etlService.createAcquisitionProjectForUsersETL(compKey);
        return response.put("message", "New project created successfully");
    }

}
