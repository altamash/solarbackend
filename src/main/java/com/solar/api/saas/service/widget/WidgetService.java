package com.solar.api.saas.service.widget;

import com.solar.api.saas.model.widget.Widget;
import com.solar.api.saas.model.widget.WidgetGlobalPermission;
import com.solar.api.saas.model.widget.WidgetURI;

import java.util.List;

public interface WidgetService {

    // Widget ////////////////////////////////////////////////
    Widget addOrUpdate(Widget widget);

    Widget findById(Long id);

    List<Widget> findAll();

    void delete(Long id);

    void deleteAll();

    // WidgetURI ////////////////////////////////////////////////
    WidgetURI addOrUpdateWidgetURI(WidgetURI widgetURI);

    WidgetURI findWidgetURIById(Long id);

    List<WidgetURI> findAllWidgetURIs();

    void deleteWidgetURI(Long id);

    void deleteAllWidgetURIs();

    // WidgetGlobalPermission ////////////////////////////////////////////////
    WidgetGlobalPermission addOrUpdate(WidgetGlobalPermission widgetGlobalPermission);

    WidgetGlobalPermission findWidgetGlobalPermissionById(Long id);

    List<WidgetGlobalPermission> findAllWidgetGlobalPermissions();

    void deleteWidgetGlobalPermission(Long id);

    void deleteAllWidgetGlobalPermissions();
}
