package com.solar.api.saas.service.process.upload.mapper;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MonitorReadingDailyMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorReadingDailyMapper.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT); //for db

    private static SimpleDateFormat givenDtFormat = new SimpleDateFormat(Utility.MONTH_DATE_YEAR_FORMAT_HYPHEN);

    public static MonitorReadingDaily toMonitorReadingDaily(MonitorReadingDailyCSV monitorReadingDailyImport, String subscriptionIdMongo) throws ParseException {
        if (monitorReadingDailyImport == null) {
            return null;
        }
        Date date = null;
        String dateStr;
        if (monitorReadingDailyImport.getYears() != null && !monitorReadingDailyImport.getYears().equals("")) {
            dateStr =  "01-01-"+monitorReadingDailyImport.getYears().replaceAll("\\D", "");;  //MM-dd-yyyy
            date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));
        }

        else if (monitorReadingDailyImport.getMonths() != null && !monitorReadingDailyImport.getMonths().equals("")) {
            //mm-yyyyy
            Object[] dateArray = Arrays.stream(monitorReadingDailyImport.getMonths().split("-")).toArray();
            dateStr = dateArray[0]+"-01-"+dateArray[1];//mm-ddd-yyyy
            date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));
        }
        //mm-dd-yyyyy
        else if (monitorReadingDailyImport.getDays()!= null && !monitorReadingDailyImport.getDays().equals("")) {
            dateStr = monitorReadingDailyImport.getDays();
            date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));
        }
       else if (monitorReadingDailyImport.getQuarters()!= null && !monitorReadingDailyImport.getQuarters().equals("")) {
            String quarter = monitorReadingDailyImport.getQuarters();
            //mm-dd-yyyy
            if(quarter.contains("Q1")){ //2023-Q1
               dateStr = "03-01-"+ Arrays.stream(quarter.split("-")).toArray()[0];
                date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));

            }
            else if(quarter.contains("Q2")){ //2023-Q2
                dateStr = "06-01-"+ Arrays.stream(quarter.split("-")).toArray()[0];
                date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));

            }
            else if(quarter.contains("Q3")){ //2023-Q3
                dateStr = "09-01-"+ Arrays.stream(quarter.split("-")).toArray()[0];
                date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));

            }
            else if(quarter.contains("Q4")){ //2023-Q4
                dateStr = "12-01-"+ Arrays.stream(quarter.split("-")).toArray()[0];
                date = dateFormat.parse(dateFormat.format(givenDtFormat.parse(dateStr)));

            }
        }
        MonitorReadingDaily.MonitorReadingDailyBuilder monitorReadingDailyBuilder = MonitorReadingDaily.builder()
//                .action(monitorReadingDailyImport.getAction())
                .day(date)
                .yieldValue(monitorReadingDailyImport.getProjected() == null? null : monitorReadingDailyImport.getProjected())
                .createdAt(LocalDateTime.now())
                .subscriptionIdMongo(subscriptionIdMongo);
        return monitorReadingDailyBuilder.build();
    }

    public static List<MonitorReadingDaily> toMonitorReadingDailyList(List<MonitorReadingDailyCSV> monitorReadingDailyCSVS, String subscriptionIdMongo) throws ParseException {
        return monitorReadingDailyCSVS.stream().map(mrd -> {
            try {
                return toMonitorReadingDaily(mrd, subscriptionIdMongo);
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        }).collect(Collectors.toList());
    }


    public static MonitorReadingDaily toUpdatedMonitorReadingDaily(MonitorReadingDaily mrdDB, MonitorReadingDaily mrdImport) {
        mrdDB.setYieldValue(mrdImport.getYieldValue() == null? null : mrdImport.getYieldValue());
        return mrdDB;
    }
}
