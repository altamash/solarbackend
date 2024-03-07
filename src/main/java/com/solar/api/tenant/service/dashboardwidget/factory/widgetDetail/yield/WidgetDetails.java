package com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.yield;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.Details;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.yield.SelectOption;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetDetails implements Details {
    private String heading;
    private Boolean isIcons;
    private Boolean isDropdown;
    private String dropdownName;
    private String dropdownSelectedValue;
    private List<SelectOption> dropdownItems;
    private List<Data> data;
/*
           details: {
          heading: 'Yield',
          isIcons: true,
          isDropdown: true,
          dropdownSelectedValue: 'December 2023',
          dropdownItems: [
            {
              label: 'December 2023',
              value: 'December 2023'
            },
            {
              label: 'January 2024',
              value: 'January 2024'
            }],

            data: [
              {
                name: 'Daily Yeild',
                value: 10.2,
                color: '#212121;',
                valueColor: '#2AAA5D',
                unit: 'kWh',
                icon: 'assets/icons/dashboard-icons/daily-yield.svg',
                height: '2.5rem',
                width: '2.5rem'
              },
              {
                name: 'Monthly Yeild',
                value: 53.7,
                unit: 'kWh',
                color: '#212121;',
                valueColor: '#2AAA5D',
                icon: 'assets/icons/dashboard-icons/monthly-yield.svg',
                height: '2.5rem',
                width: '2.5rem'
              },
              {
                name: 'Annual Yeild',
                value: 104.2,
                unit: 'kWh',
                color: '#212121;',
                valueColor: '#2AAA5D',
                icon: 'assets/icons/dashboard-icons/annual-yield.svg',
                height: '2.5rem',
                width: '2.5rem'
              },
              {
                name: 'Annual Yeild',
                value: 456.2,
                unit: 'kWh',
                color: '#212121;',
                valueColor: '#2AAA5D',
                icon: 'assets/icons/dashboard-icons/total-yield.svg',
                height: '2.5rem',
                width: '2.5rem'
              }
            ]
        }
*/

}
