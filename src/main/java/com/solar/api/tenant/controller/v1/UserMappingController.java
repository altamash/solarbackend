package com.solar.api.tenant.controller.v1;


import com.solar.api.tenant.mapper.user.UserMappingDTO;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.service.userMapping.UserMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("UserMappingController")
@RequestMapping(value = "/user/mapping")
public class UserMappingController {

    @Autowired
    private UserMappingService userMappingService;

    @PostMapping("/postUserMapping")
    public UserMapping postUserMapping(@RequestBody UserMappingDTO userMappingDTO) {
        return userMappingService.save(userMappingDTO);
    }

    @GetMapping("/getAllUserMapping")
    public List<UserMapping> getAllUserMapping() {
        List<UserMapping> userMappingList = userMappingService.findAll();
        return userMappingList;
    }
    @PutMapping("/updateUserMapping/{id}")
    public UserMapping updateUserMapping(@PathVariable("id") Long id, @RequestBody UserMappingDTO userMappingDTO) {
        return userMappingService.update(id, userMappingDTO);
    }

    @DeleteMapping("/deleteUserMapping/{id}")
    public void deleteUserMapping(@PathVariable("id") Long id) {
        userMappingService.delete(id);
    }
}
