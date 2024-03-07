package com.solar.api.saas.model.permission.navigation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApplicationScope
@Component
public class InMemoryNavigationElement {

    private List<NavigationElement> navigationElements = new ArrayList<>();
}
