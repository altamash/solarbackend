package com.solar.api.tenant.service.extended.register;

import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.extended.register.RegisterMapper;
import com.solar.api.tenant.model.extended.register.InMemoryRegisterHierarchy;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import com.solar.api.tenant.repository.RegisterHierarchyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class RegisterHierarchyServiceImpl implements RegisterHierarchyService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private RegisterHierarchyRepository repository;
    @Autowired
    private InMemoryRegisterHierarchy inMemoryRegisterHierarchy;

    @Override
    public RegisterHierarchy save(RegisterHierarchy hierarchy) {
        hierarchy = repository.save(hierarchy);
        addInMemoryRegisterHierarchies();
        return hierarchy;
    }

    @Override
    public void saveAll(List<RegisterHierarchy> hierarchies) {
        repository.saveAll(hierarchies);
    }

    @Override
    public RegisterHierarchy update(RegisterHierarchy hierarchy) {
        RegisterHierarchy hierarchyDb = findById(hierarchy.getId());
        hierarchy = RegisterMapper.toUpdatedRegisterHierarchy(hierarchyDb, hierarchy);
        if (hierarchy.getCode() == null || hierarchy.getCode().isEmpty()) {
            throw new SolarApiException("code is required.");
        }
        hierarchy = repository.save(hierarchy);
        addInMemoryRegisterHierarchies();
        return hierarchy;
    }

    @Override
    public RegisterHierarchy findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(RegisterHierarchy.class, id));
    }

    @Override
    public List<RegisterHierarchy> findByLevel(Integer level) {
        return repository.findByLevel(level);
    }

    @Override
    public List<RegisterHierarchy> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public RegisterHierarchy findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<RegisterHierarchy> findByParent(String parent) {
        return repository.findByParent(parent);
    }

    @Override
    public List<RegisterHierarchy> findByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Override
    public List<RegisterHierarchy> findSubLevelsByLevel(Integer level) {
        return repository.findByLevel(++level);
    }

    @Override
    public List<RegisterHierarchy> findAll() {
        return repository.findAll();
    }

    @Override
    public InMemoryRegisterHierarchy addInMemoryRegisterHierarchies() {
        List<RegisterHierarchy> hierarchies = repository.findByLevel(1);
        for (RegisterHierarchy hierarchy : hierarchies) {
            try {
                setSubHierarchies(hierarchy);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        inMemoryRegisterHierarchy.clearHierarchies();
        inMemoryRegisterHierarchy.addHierarchies(hierarchies);
        return inMemoryRegisterHierarchy;
    }

    private void setSubHierarchies(RegisterHierarchy hierarchy) {
        List<RegisterHierarchy> subHierarchies = repository.findByParentId(hierarchy.getId());
        hierarchy.setSubHierarchies(subHierarchies);
        for (RegisterHierarchy h : subHierarchies) {
            subHierarchies = repository.findByParentId(h.getId());
            if (!subHierarchies.isEmpty()) {
                setSubHierarchies(h);
            }
        }
    }

    @Override
    public List<RegisterHierarchy> getInMemoryRegisterHierarchies() {
        if (inMemoryRegisterHierarchy.getRegisterHierarchies().isEmpty()) {
            addInMemoryRegisterHierarchies();
        }
        return inMemoryRegisterHierarchy.getRegisterHierarchies();
    }

    @Override
    public RegisterHierarchy getInMemoryRegisterHierarchyById(Long id) {
        if (inMemoryRegisterHierarchy.getRegisterHierarchies().isEmpty()) {
            addInMemoryRegisterHierarchies();
        }
        return getInMemoryRegisterHierarchiesById(id, inMemoryRegisterHierarchy.getRegisterHierarchies());
    }

    private RegisterHierarchy getInMemoryRegisterHierarchiesById(Long id, List<RegisterHierarchy> registerHierarchies) {
        RegisterHierarchy hierarchy = null;
        if (registerHierarchies != null) {
            for (RegisterHierarchy h : registerHierarchies) {
                if (h.getId().longValue() == id) {
                    hierarchy = h;
                }
                if (hierarchy == null) {
                    hierarchy = getInMemoryRegisterHierarchiesById(id, h.getSubHierarchies());
                } else {
                    break;
                }
            }
        }
        return hierarchy;
    }

    @Override
    public RegisterHierarchy getTopLevelHierarchy(Long id) {
        return getTopHierarchy(id);
    }

    private RegisterHierarchy getTopHierarchy(Long id) {
        RegisterHierarchy registerHierarchy = findById(id);
        if (registerHierarchy.getLevel().longValue() == 1) {
            return registerHierarchy;
        }
        return getTopLevelHierarchy(registerHierarchy.getParentId());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
        addInMemoryRegisterHierarchies();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
        addInMemoryRegisterHierarchies();
    }
}
