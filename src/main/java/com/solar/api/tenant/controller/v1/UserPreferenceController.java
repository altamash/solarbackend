package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.preferences.UserPreferenceDTO;
import com.solar.api.tenant.mapper.preferences.UserPreferenceMapper;
import com.solar.api.tenant.service.preferences.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UserPreferenceController")
@RequestMapping(value = "/userPreference")
public class UserPreferenceController {
    @Autowired
    private UserPreferenceService userPreferenceService;

    @PostMapping("/add")
    public UserPreferenceDTO add(@RequestBody UserPreferenceDTO userPreferenceDTO) throws Exception {
        return UserPreferenceMapper.toUserPreferenceDTO(
                userPreferenceService.add(UserPreferenceMapper.toUserPreference(userPreferenceDTO)));
    }

    @PutMapping("/edit")
    public UserPreferenceDTO update(@RequestBody UserPreferenceDTO userPreferenceDTO) throws Exception {
        return UserPreferenceMapper.toUserPreferenceDTO(
                userPreferenceService.update(UserPreferenceMapper.toUserPreference(userPreferenceDTO)));
    }

    @GetMapping("/{id}")
    public UserPreferenceDTO findById(@PathVariable Long id) throws Exception {
        return UserPreferenceMapper.toUserPreferenceDTO(userPreferenceService.findById(id));
    }

    @GetMapping("/getAll")
    public List<UserPreferenceDTO> findAll() throws Exception {
        return UserPreferenceMapper.toUserPreferenceDTOList(userPreferenceService.findAll());
    }
}
