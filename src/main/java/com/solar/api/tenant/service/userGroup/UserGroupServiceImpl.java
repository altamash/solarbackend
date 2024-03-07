package com.solar.api.tenant.service.userGroup;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.tiles.UserGroupResourceTile;
import com.solar.api.tenant.mapper.user.userGroup.EmployeeDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupMapper;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupMapper;
import com.solar.api.tenant.model.userGroup.UserGroup;
import com.solar.api.tenant.repository.UserGroup.EntityGroupRepository;
import com.solar.api.tenant.repository.UserGroup.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    EntityGroupRepository entityGroupRepository;

    @Override
    public UserGroup addOrUpdate(UserGroup userGroup) {
        if (userGroup.getId() != null) {
            UserGroup userGroupData = findById(userGroup.getId());
            if (userGroupData == null) {
                throw new NotFoundException(UserGroup.class, userGroup.getId());
            }
            return userGroupRepository.save(UserGroupMapper.toUpdateUserGroup(userGroupData,
                    userGroup));
        }
        if (userGroup.getRefType() != null) {
            if (checkIfExist(userGroup)) {
                throw new AlreadyExistsException(UserGroup.class, userGroup.getRefType(), userGroup.getRefId());
            } else {
                return userGroupRepository.save(userGroup);
            }
        } else {
            return userGroupRepository.save(userGroup);
        }
    }

    private boolean checkIfExist(UserGroup userGroup) {
        return findByRefIdAndRefTypeAndStatus(userGroup.getRefId(), userGroup.getRefType(), userGroup.isStatus()) != null;
    }

    @Override
    public List<UserGroup> addAll(List<UserGroup> userGroups) {
        return userGroupRepository.saveAll(userGroups);
    }

    @Override
    public UserGroup findById(Long id) {
        return userGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserGroup> findAll() {
        List<UserGroup> userGroups = userGroupRepository.findAll();
        return userGroups;
    }

    @Override
    public UserGroup getByGroupName(String groupName) {
        return userGroupRepository.findByUserGroupName(groupName);
    }

    @Override
    public UserGroup get(Long id) {
        return userGroupRepository.findById(id).orElseThrow(() -> new NotFoundException(UserGroup.class, id));
    }

    @Override
    public UserGroup activateUserGroup(Long groupId, Boolean status) {
        Optional<UserGroup> userGroup = userGroupRepository.findById(groupId);
        if (!userGroup.isPresent()) {
            throw new NotFoundException("No User Group Found");
        }
        userGroup.get().setActive(status);
        return userGroupRepository.save(userGroup.get());
    }

    @Override
    public List<EmployeeDTO> findExistingResourcesByGroup(Long groupId) {
        List<EmployeeDTO> data = userGroupRepository.findAllEmployeesByUserGroupId(groupId);

        return data;
        //TODO close due to filter by designation
//        return data.stream()
//                .filter(distinctByKey(EmployeeDTO::getEmployeeDesignation))
//                .collect(Collectors.toList());
    }

    @Override
    public UserGroup save(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }

    @Override
    public UserGroup findByRefIdAndRefTypeAndStatus(String refId, String refType, boolean status) {
        return userGroupRepository.findByRefIdAndRefTypeAndStatus(refId, refType, status);
    }

    @Override
    public List<UserGroup> findUserGroupByParentId(String parentId) {
        return userGroupRepository.findByParentId(parentId);
    }

    @Override
    public UserGroup findByRefIdAndStatus(String refId, boolean status) {
        return userGroupRepository.findByRefIdAndStatus(refId, status);
    }

    @Override
    public UserGroup removeUserGroupById(Long id) {
        Optional<UserGroup> userGroup = userGroupRepository.findById(id);
        if (userGroup.isEmpty()) {
            throw new NotFoundException("No User Group Found");
        }
        userGroup.get().setIsDeleted(true);
        return userGroupRepository.save(userGroup.get());
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Api to return User Group DTO with user count
     *
     * @return
     */
    public List<UserGroupDTO> getAllUserGroupByType(String groupType) {
        return userGroupRepository.getAllByUserGroupType(groupType);
    }
    //UserGroupResourceTile

    public List<UserGroupResourceTile> getAllUserGroupByTypeResources(String groupType) {
        List<UserGroupResourceTile> userGroupResourceTiles = new ArrayList<>();
        List<UserGroupDTO> userGroupDTOS = userGroupRepository.getAllByUserGroupType(groupType);

        for (UserGroupDTO userGroupDTO : userGroupDTOS) {
            UserGroupResourceTile userGroupResourceTile = new UserGroupResourceTile();
            userGroupResourceTile.setUserGroupId(userGroupDTO.getId());
            userGroupResourceTile.setUserGroupName(userGroupDTO.getUserGroupName());
            userGroupResourceTile.setResourcesCount(userGroupDTO.getUserCount());
            userGroupResourceTile.setStatus(userGroupDTO.isStatus());
            userGroupResourceTile.setEntityGroupTiles(EntityGroupMapper.toUserGroupTiles(entityGroupRepository.findByUserGroupId(userGroupDTO.getId())));
            userGroupResourceTiles.add(userGroupResourceTile);
        }
        return userGroupResourceTiles;
    }
    @Override
    public UserGroup findByRefIdAndRefTypeAndStatusAndIsDeleted(String refId, String refType, boolean status, boolean isDeleted) {
        return userGroupRepository.findByRefIdAndRefTypeAndStatusAndIsDeleted(refId, refType, status,isDeleted);
    }

}
