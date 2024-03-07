package com.solar.api.saas.service.widget;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.widget.WidgetMapper;
import com.solar.api.saas.model.widget.Widget;
import com.solar.api.saas.model.widget.WidgetGlobalPermission;
import com.solar.api.saas.model.widget.WidgetURI;
import com.solar.api.saas.repository.WidgetGlobalPermissionsRepository;
import com.solar.api.saas.repository.WidgetRepository;
import com.solar.api.saas.repository.WidgetURIRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("masterTransactionManager")
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;
    private final WidgetURIRepository widgetURIRepository;
    private final WidgetGlobalPermissionsRepository permissionsRepository;

    WidgetServiceImpl(WidgetRepository widgetRepository, WidgetURIRepository widgetURIRepository,
                      WidgetGlobalPermissionsRepository permissionsRepository) {
        this.widgetRepository = widgetRepository;
        this.widgetURIRepository = widgetURIRepository;
        this.permissionsRepository = permissionsRepository;
    }

    // Widget ////////////////////////////////////////////////
    @Override
    public Widget addOrUpdate(Widget widget) {
        if (widget.getId() != null) {
            Widget widgetDb = findById(widget.getId());
            widgetRepository.save(WidgetMapper.toUpdatedWidget(widgetDb, widget));
        }
        return widgetRepository.save(widget);
    }

    @Override
    public Widget findById(Long id) {
        return widgetRepository.findById(id).orElseThrow(() -> new NotFoundException(Widget.class, id));
    }

    @Override
    public List<Widget> findAll() {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void deleteAll() {

    }

    // WidgetURI ////////////////////////////////////////////////
    @Override
    public WidgetURI addOrUpdateWidgetURI(WidgetURI widgetURI) {
        return null;
    }

    @Override
    public WidgetURI findWidgetURIById(Long id) {
        return null;
    }

    @Override
    public List<WidgetURI> findAllWidgetURIs() {
        return null;
    }

    @Override
    public void deleteWidgetURI(Long id) {

    }

    @Override
    public void deleteAllWidgetURIs() {

    }

    // WidgetGlobalPermission ////////////////////////////////////////////////
    @Override
    public WidgetGlobalPermission addOrUpdate(WidgetGlobalPermission widgetGlobalPermission) {
        return null;
    }

    @Override
    public WidgetGlobalPermission findWidgetGlobalPermissionById(Long id) {
        return null;
    }

    @Override
    public List<WidgetGlobalPermission> findAllWidgetGlobalPermissions() {
        return null;
    }

    @Override
    public void deleteWidgetGlobalPermission(Long id) {

    }

    @Override
    public void deleteAllWidgetGlobalPermissions() {

    }
}
