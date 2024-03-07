package com.solar.api.saas.service.systemAttribute;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.attribute.SystemAttribute;
import com.solar.api.saas.repository.SystemAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.solar.api.saas.mapper.attribute.systemAttribute.SystemAttributeMapper.toUpdatedSystemAttribute;

@Service
//@Transactional("masterTransactionManager")
public class SystemAttributesServiceImpl implements SystemAttributeService {

    @Autowired
    SystemAttributeRepository repository;

    @Override
    public SystemAttribute save(SystemAttribute systemAttribute) {
        if (repository.findByAttributeKey(systemAttribute.getAttributeKey()).isPresent()) {
            throw new AlreadyExistsException(SystemAttribute.class, "attributeKey", systemAttribute.getAttributeKey());
        }
        return repository.save(systemAttribute);
    }

    @Override
    public SystemAttribute update(SystemAttribute systemAttribute) {
        if (systemAttribute.getId() != null) {
            SystemAttribute applicationSettingByAttributeKey = findByAttributeKey(systemAttribute.getAttributeKey());
            if (applicationSettingByAttributeKey != null && systemAttribute.getId() != applicationSettingByAttributeKey.getId()) {
                throw new AlreadyExistsException(SystemAttribute.class, "attributeKey", systemAttribute.getAttributeKey());
            }
            SystemAttribute systemAttributeDB = findById(systemAttribute.getId());
            if (systemAttributeDB == null) {
                throw new NotFoundException(SystemAttribute.class, systemAttribute.getId());
            }
            systemAttributeDB = toUpdatedSystemAttribute(systemAttributeDB, systemAttribute);
            return repository.save(systemAttributeDB);
        }
        throw new InvalidValueException("id");
    }

    @Override
    public SystemAttribute findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(SystemAttribute.class, id));
    }

    @Override
    public SystemAttribute findByAttributeKey(String attributeKey) {
        return repository.findByAttributeKey(attributeKey).orElse(null);
    }

    public SystemAttribute findByAttribute(String attribute) {
        return repository.findByAttribute(attribute);
    }

    public List<SystemAttribute> findByParentAttribute(String parentAttribute) {
        return repository.findByParentAttribute(parentAttribute);
    }

    @Override
    public List<SystemAttribute> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public SystemAttribute findAllCarouselImagesByChannel(String channel) throws IOException {
        String webKey = "CAROUSEL_WEB_KEY";
        String mobileKeyKey = "CAROUSEL_MOBILE_KEY";
        if (channel.equals("web")) {
            channel = webKey;
        }
        if (channel.equals("mobile")) {
            channel = mobileKeyKey;
        }
        return repository.findByAttributeKey(channel).orElseThrow(() -> new NotFoundException(SystemAttribute.class, (Long) null));
    }
}
