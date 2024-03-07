package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.preferences.UserPreferenceDetailDTO;
import com.solar.api.tenant.mapper.preferences.UserPreferenceDetailMapper;
import com.solar.api.tenant.service.preferences.UserPreferenceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UserPreferenceDetailController")
@RequestMapping(value = "/userPreferenceDetail")
public class UserPreferenceDetailController {
    @Autowired
    private UserPreferenceDetailService userPreferenceDetailService;

    @PostMapping("/add")
    public UserPreferenceDetailDTO add(@RequestBody UserPreferenceDetailDTO userPreferenceDetailDTO) throws Exception {
        return UserPreferenceDetailMapper.toUserPreferenceDetailDTO(
                userPreferenceDetailService.add(UserPreferenceDetailMapper.toUserPreferenceDetail(userPreferenceDetailDTO)));
    }

    @PutMapping("/edit")
    public UserPreferenceDetailDTO update(@RequestBody UserPreferenceDetailDTO userPreferenceDetailDTO) throws Exception {
        return UserPreferenceDetailMapper.toUserPreferenceDetailDTO(
                userPreferenceDetailService.update(UserPreferenceDetailMapper.toUserPreferenceDetail(userPreferenceDetailDTO)));
    }

    @GetMapping("/{id}")
    public UserPreferenceDetailDTO findById(@PathVariable Long id) throws Exception {
        return UserPreferenceDetailMapper.toUserPreferenceDetailDTO(userPreferenceDetailService.findById(id));
    }

    @GetMapping("/getAll")
    public List<UserPreferenceDetailDTO> findAll() throws Exception {
        return UserPreferenceDetailMapper.toUserPreferenceDetailDTOList(userPreferenceDetailService.findAll());
    }
}
