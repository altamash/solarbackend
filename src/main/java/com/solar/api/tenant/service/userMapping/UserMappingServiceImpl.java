package com.solar.api.tenant.service.userMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.tenant.mapper.user.UserMappingDTO;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.repository.userMapping.UserMappingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMappingServiceImpl implements UserMappingService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserMappingRepo userMappingRepo;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public UserMapping save(UserMappingDTO userMappingDTO) {
        UserMapping userMapping1 = objectMapper.convertValue(userMappingDTO , UserMapping.class);
        return userMappingRepo.save(userMapping1);
    }

    @Override
    public List<UserMapping> findAll() {
       List<UserMapping> userMappingDTOList = userMappingRepo.findAll();
       return userMappingDTOList;
    }

    @Override
    public UserMapping update(Long id, UserMappingDTO userMappingDTO) {
        try {
            Optional<UserMapping> userMappingList = userMappingRepo.findById(id);
            if (userMappingList.isPresent()) {
                UserMapping existingUserMapping = userMappingList.get();
                existingUserMapping.setModule(userMappingDTO.getModule());
                existingUserMapping.setRef_id(userMappingDTO.getRef_id());
                existingUserMapping.setEntityId(userMappingDTO.getEntityId());

                UserMapping updatedUserMapping = userMappingRepo.save(existingUserMapping);
                return updatedUserMapping;
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Error while updating user mapping");
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        try{
            userMappingRepo.deleteById(id);
            LOGGER.info("User mapping deleted successfully");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Error while deleting user mapping");
        }
    }

    @Override
    public List<UserMapping> findByEntityIdsNotIn(List<Long> entityIds) {
        return userMappingRepo.findByEntityIdNotIn(entityIds);
    }
}
