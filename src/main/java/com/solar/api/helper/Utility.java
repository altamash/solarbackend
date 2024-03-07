package com.solar.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.extended.document.DocuMapper;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAuthHeaders;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.pvmonitor.WeekInfoDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.CompanyPreferenceRepository;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.SuccessDTO;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.io.IOUtils.toByteArray;

@Component
public class Utility {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);
    public static final String DURATION_FORMAT_SHORT = "HH:mm:ss";
    public static final String INVOICE_SHORT_MONTH_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String SYSTEM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SYSTEM_S_SSDT_FORMAT = "E MMM d HH:mm:ss yyyy";
    public static final String CUSTOM_DATE_TIME_FORMAT = "yyyy/MM/dd'T'HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String MONTH_YEAR_FORMAT = "MM-yyyy";
    public static final String MONTH_YEAR_SHORT_FORMAT = "MMM yyyy";

    public static final String FIRST = "FIRST";
    public static final String MONTH_DATE_YEAR_FORMAT = "MM/dd/yyyy";
    public static final String SYSTEM_DATE_TIME_SIMPLE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String YEAR_MONTH_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "MMM dd, yyyy";
    public static final String YEAR_MONTH_DATE = "yyyyMMdd";
    public static final String YEAR_MON_DATE_HH_MM_SS_SS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String YEAR_MON_DATE_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String WEATHER_DATA_DATE_FORMAT = "MMMM dd, yyyy, EEEE";

    public static final String MONTH_FORMAT = "MMM";

    public static final String MONTH_DATE_YEAR_FORMAT_HYPHEN = "MM-dd-yyyy";
    public static final String MON_DAY_YEAR_H_MM_A = "MMM dd, yyyy h:mm a";
    public static final String CAPITAL_MONTH_YEAR_FORMAT = "MMMM yyyy";
    public static final String YEAR_MONTH_DAY_TIME_HOUR_OFFSET = "yyyy-MM-dd'T'HH:mm:ssZ";
    //    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String DAY_MON_DATE_HH_MM_SS_ZONE_YEAR = "EEE MMM dd HH:mm:ss z yyyy";

    public static final String DAY_FORMAT = "dd";
    public static final DateTimeFormatter YEAR_MON_DATE_HH_MM_SS_FORMATTER = DateTimeFormatter.ofPattern(YEAR_MON_DATE_HH_MM_SS);
    public static final DateTimeFormatter DAY_MON_DATE_HH_MM_SS_ZONE_YEAR_FORMATTER = DateTimeFormatter.ofPattern(DAY_MON_DATE_HH_MM_SS_ZONE_YEAR);
    public static final DateTimeFormatter YEAR_MONTH_DATE_FORMATTER = DateTimeFormatter.ofPattern(YEAR_MONTH_DATE_FORMAT);
    public static final String FULL_MONTH_DATE_YEAR_FORMAT = "MMMM dd, yyyy";
    @Autowired
    private CompanyPreferenceRepository companyPreferenceRepository;

    @Autowired
    private static EmailService emailService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;

    @Value("${app.profile}")
    private String appProfile;


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ParseException {
        getTempFile();
        CronExpression springCron = new CronExpression("0 0/30 * 1/1 * ?");
        CronExpression ce = new CronExpression("0 0/30 * 1/1 * ?");
        ce.getNextValidTimeAfter(new Date());
        Date date = springCron.getFinalFireTime();
        System.out.println("Cron Expression: " + date);
        System.out.println("Cron Summary: " + springCron.getExpressionSummary());
        System.out.println("Cron TimeZone: " + springCron.getTimeZone());

        // Step 1 - encode password with MD5 encryption
        String md5Password = getMD5String("123456789");
        // Step 2 - get Content-MD5 from body MD5 string
        String bodyJsonString =
                new ObjectMapper().writeValueAsString(MonitorAPIAuthBody.builder()
                        .userInfo("egvmw1976@gmail.com")
                        .passWord(md5Password)
                        .yingZhenType(1)
                        .language("2").build()
                );
        MonitorAuthHeaders monitorAuthHeaders = getAuthData(bodyJsonString, "/user/login2");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("time", Arrays.asList(monitorAuthHeaders.getTime()));
        headers.put("content-md5", Arrays.asList(monitorAuthHeaders.getContent()));
        headers.put("authorization", Arrays.asList(monitorAuthHeaders.getAuth()));
        headers.put("content-type", Arrays.asList("application/json;charset=UTF-8"));
        WebUtils.submitRequest(HttpMethod.POST, "https://www.soliscloud.com:15555/user/login2", bodyJsonString,
                headers, Object.class);
    }

    public static void batchNotification(String jobName, Long jobId, String stackTrace, String subject) {
        try {
            emailService.batchNotification(jobName, jobId, stackTrace, subject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date addDays(Date date, Integer days) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusDays(days);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date deductDays(Date date, Integer days) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusDays(days);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addMonths(Date date, Integer months) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusMonths(months);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addMinutes(Date date, Integer minutes) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusMinutes(minutes);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addMinutesInGMT(Date date, Integer minutes) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).plusMinutes(minutes);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date minusMinutes(Date date, Integer minutes) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusMinutes(minutes);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long addMinstoEpoc(Date date, Integer minutes) {
        return LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault()).plusMinutes(minutes).toEpochSecond(ZoneOffset.UTC);
    }

    public static String readableDateFormat(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date getDate(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        try {
            return dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getDateString(Date date, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static long chronoUnitBetween(ChronoUnit chronoUnit, Date date1, Date date2) {
        return ChronoUnit.valueOf(chronoUnit.name()).between(
                LocalDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(date2.toInstant(), ZoneId.systemDefault()));
    }

    public static String parseDaySuffix(Date date) {
        DateTime dateTime = new DateTime(date);
        String formatFirst = "MMM d'st', YYYY";
        String formatSecond = "MMM d'nd', YYYY";
        String formatThird = "MMM d'rd', YYYY";
        String formatStd = "MMM d'th', YYYY";
        String pattern;
        switch (dateTime.getDayOfMonth()) {
            case 1:
            case 21:
            case 31:
                pattern = formatFirst;
                break;
            case 2:
            case 22:
                pattern = formatSecond;
                break;
            case 3:
            case 23:
                pattern = formatThird;
                break;
            default:
                pattern = formatStd;
        }
        String output = dateTime.toString(pattern, Locale.ENGLISH);
        System.out.println(output);
        return output;
    }

    public static boolean isBefore(Date date1, Date date2) {
        return new DateTime(date1).isBefore(new DateTime(date2));
    }

    public static boolean isBeforeOrEqual(Date date1, Date date2) {
        return new DateTime(date1).isBefore(new DateTime(date2)) ||
                new DateTime(date1).isEqual(new DateTime(date2));
    }

    public static String formatDuration(long duration, String format, boolean padWithZeros) {
        return DurationFormatUtils.formatDuration(duration, format, padWithZeros);
    }

    public static Date localDateToUtilDate(LocalDate dateTime) {
        return dateTime.toDateTimeAtStartOfDay().toDate();
    }

    public static String formatLocalDate(LocalDate date, String format) {
        return date.toString(DateTimeFormat.forPattern(format));
    }

    public static Date getStartOfMonth(String date, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date dt = dateFormat.parse(date);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getStartOfMonth(Date date, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date dt = getDate(date, SYSTEM_DATE_FORMAT);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getEndOfMonth(String date, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date dt = dateFormat.parse(date);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    //method to get start of the year which returns LocalDateTime

    public static Date getStartOfYear(String date, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date dt = dateFormat.parse(date);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_YEAR, cal.getMinimum(Calendar.DAY_OF_YEAR));
        return cal.getTime();
    }

    //method to get end of the year
    public static Date getEndOfYear(String date, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date dt = dateFormat.parse(date);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        return cal.getTime();
    }

    public static Date getEndOfMonth(Date date, String format) {
        Calendar cal = Calendar.getInstance();
        Date dt = getDate(date, format);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getStartOfDate(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.MILLISECOND, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.HOUR_OF_DAY, 0);
        return day.getTime();
    }

    public static Date getStartOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        Date dt = getDate(date, SYSTEM_DATE_FORMAT);
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static boolean isYearBefore(Date param1, Date param2) {
        if (param1.toInstant().isBefore(param2.toInstant())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMonthBefore(Date param1, Date param2) {
        if (param1.toInstant().isBefore(param2.toInstant())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDayBefore(LocalDateTime param1, LocalDateTime param2) {
        if (param1.getDayOfMonth() == param2.minusDays(1).getDayOfMonth()) {
            return true;
        } else {
            return false;
        }
    }


    public static Date getStartOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Objects.requireNonNull(getDate(date, SYSTEM_DATE_FORMAT)));
        cal.add(Calendar.MONTH, -(cal.get(Calendar.MONTH)));
        cal.set(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getEndOfDate(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.MILLISECOND, day.getMaximum(Calendar.MILLISECOND));
        day.set(Calendar.SECOND, day.getMaximum(Calendar.SECOND));
        day.set(Calendar.MINUTE, day.getMaximum(Calendar.MINUTE));
        day.set(Calendar.HOUR_OF_DAY, day.getMaximum(Calendar.HOUR_OF_DAY));
        return day.getTime();
    }

    public static boolean areInSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean areInSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static String getFormattedMillis(long millis) {
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static LocalDateTime subtractHours(Date stDate, String operator, String hours, String min) {
        if (operator.contains("-")) {
            return Utility.toLocalDateTimeEastUs(stDate);
        } else {
            return Utility.toLocalDateTimeEastUs(stDate);
        }
    }

    public static String getDeviceUrl(String mpJson) {
        return Utility.getMeasureAsJson(mpJson, Constants.RATE_CODES.DEVICE_URL);
    }

    public static List<LocalDateTime> getTimeStamps(LocalDateTime startTime, LocalDateTime endTime, int interval) {
        if (endTime.isAfter(Utility.toLocalDateTime(new Date()))) {
            LOGGER.info("Requested Time {} is after the current time {}", endTime, Utility.toLocalDateTime(new Date()));
            return new ArrayList<>();
        }
        List<LocalDateTime> dateList = new ArrayList<>();
        while (!startTime.equals(endTime)) {
            dateList.add(startTime);
            startTime = startTime.plusMinutes(interval);
        }
        return dateList;
    }

    public static List<LocalDateTime> getMonths(LocalDateTime startMonth, LocalDateTime endMonth) {
        if (endMonth.isAfter(Utility.toLocalDateTime(new Date()))) {
            LOGGER.info("Requested Time {} is after the current time {}", endMonth, Utility.toLocalDateTime(new Date()));
            return new ArrayList<>();
        }
        List<LocalDateTime> dateList = new ArrayList<>();
        dateList.add(startMonth);
        while (!startMonth.equals(endMonth)) {
            startMonth = startMonth.plusMonths(1);
            dateList.add(startMonth);
        }
        return dateList;
    }

    public static List<LocalDateTime> getDays(LocalDateTime startDay, LocalDateTime endDay) {
        if (endDay.isAfter(Utility.toLocalDateTime(new Date()))) {
            LOGGER.info("Requested Time {} is after the current time {}", endDay, Utility.toLocalDateTime(new Date()));
            return new ArrayList<>();
        }
        List<LocalDateTime> dateList = new ArrayList<>();
        dateList.add(startDay);
        while (!startDay.equals(endDay)) {
            startDay = startDay.plusDays(1);
            dateList.add(startDay);
        }
        return dateList;
    }

    public static List<Date> getDaysInDate(Date startDate, Date endDate) {
        if (endDate.after(new Date())) {
            LOGGER.info("Requested Time {} is after the current time {}", endDate, Utility.toLocalDateTime(new Date()));
            return new ArrayList<>();
        }
        List<Date> dateList = new ArrayList<>();
        dateList.add(startDate);
        while (!startDate.equals(endDate)) {
            startDate = Utility.addDays(startDate, 1);
            dateList.add(startDate);
        }
        return dateList;
    }

    public static Date getLastMonthWithFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SYSTEM_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date lastMonthDate = calendar.getTime();
        return calendar.getTime();
    }

    public Double round(Object o, int rounding) {
        if (o == null) {
            return null;
        }
        StringBuilder decimalFormat = new StringBuilder("#.");
        IntStream.range(0, rounding).forEach(i -> decimalFormat.append("#"));
        DecimalFormat df = new DecimalFormat(decimalFormat.toString());
        return Double.valueOf(df.format(o));

    }

    public Double roundBilling(Double number, int rounding) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(rounding, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static Double rounding(Object o, int rounding) {
        if (o == null) {
            return null;
        }
        double value = Double.parseDouble(o.toString());
        double roundedValue = BigDecimal.valueOf(value)
                .setScale(rounding, RoundingMode.HALF_UP)
                .doubleValue();
        return roundedValue;
    }


    public CompanyPreference getCompanyPreference() {
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
        return companyPreferenceRepository.findByCompanyKeyFetchBanners(tenant.getCompanyKey());
    }

    public Long getCompKey() {
        return getCompanyPreference().getCompanyKey();
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Convert File to Base64
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String convertFileToBase64(File file) throws IOException {
        byte[] fileData = toByteArray(new FileInputStream(file));
        return Base64.getEncoder().encodeToString(fileData);
    }

    // TODO: parameterize file name
    public static File convertBase64ToCSV(String base64) {
        File file = new File("BillingCredits.csv");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
            // To be short I use a corrupted PDF string, so make sure to use a valid one if you want to preview the
            // PDF file
            byte[] decoder = Base64.getDecoder().decode(base64);
            fileOutputStream.write(decoder);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * Encode string into base64
     *
     * @param bytes
     * @return
     */
    public static String toBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] getCSVBytes(List<String> headers, List<List> valuesList) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            printer.printRecord(headers);
            for (List values : valuesList) {
                printer.printRecord(values);
            }
            out.close();
            return os.toByteArray();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static String uploadToStorage(StorageService storageService, byte[] byteArray, String appProfile,
                                         String directoryReference, String fileName,
                                         Long compKey, Boolean relativeUrl) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(byteArray.length)) {
            os.write(byteArray, 0, byteArray.length);
            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                return storageService.uploadInputStream(is, (long) os.size(), appProfile,
                        directoryReference, fileName, compKey, relativeUrl);
            } catch (StorageException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getMD5String(String toHash)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(toHash.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

    public static String getZoneFormattedTime(Date dateTime, String zone) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        return sdf.format(dateTime);
    }

    public static String getZoneFormattedTime(Date dateTime, String zone, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        return sdf.format(dateTime);
    }


    public static File getTempFile(String... directoryAndPrefix) {
        Path path = null;
        if (directoryAndPrefix.length > 0) {
            try {
                path = Files.createTempDirectory(Paths.get(directoryAndPrefix[0]), directoryAndPrefix.length > 1 ?
                        directoryAndPrefix[1] : "");
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
        }
        File file = path.toFile();
        file.deleteOnExit();
        return file;
    }

    private static MonitorAuthHeaders getAuthData(String bodyJsonString, String api) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String contentMd5 = getMD5String(bodyJsonString);
        String md5ToBase64Content = getMD5ToBase64Encoded(contentMd5);
        String formattedTime = getZoneFormattedTime(new Date(), "GMT");
        String key = "POST\n" +
                md5ToBase64Content +
                "\napplication/json\n" +
                formattedTime + "\n" +
                api;
        String auth = "WEB 2424:" + Base64.getEncoder().encodeToString(new HmacUtils(HmacAlgorithms.HMAC_SHA_1,
                "5704383536604a8bb94c83ebc059aa8c")
                .hmac(key.getBytes("utf8")));
        return MonitorAuthHeaders.builder()
                .content(md5ToBase64Content)
                .time(formattedTime)
                .auth(auth)
                .build();
    }

    private static String getMD5ToBase64Encoded(String e) {
        int a = 0;
        int t = e.length();
        if (t % 2 != 0)
            return null;
        t /= 2;
        byte[] i = new byte[t];
        for (int l = 0; l < t; l++) {
            String r = e.substring(a, 2 + a);
            int n = Integer.parseInt(r, 16);
            if (n > 128) {
                n -= 256;
            }
            i[l] = (byte) n;
            a += 2;
        }
        return Base64.getEncoder().encodeToString(i);
    }

    public static List<String> getMonthYearFromDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        List<String> yearMonth = new ArrayList<>();
        yearMonth.add(String.valueOf(calendar.get(Calendar.YEAR)));
        yearMonth.add(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        return yearMonth;
    }

    public static Date getDateBeforeNow(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.SECOND, -1);
        return day.getTime();
    }

    public static long getDifferenceDays(Date d1, Date d2) {

        long diffInMillies = Math.abs(d1.getTime() - d2.getTime());
        long difference = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return difference;
    }

    /* Regex */
    public static List<String> findBetween(String string, String delimLeft, String delimRIght) {
        Pattern p = Pattern.compile(delimLeft + "(.*?)" + delimRIght);
        Matcher m = p.matcher(string);
        List<String> placeholders = new ArrayList<>();
        while (m.find()) {
            placeholders.add(m.group(1));
        }
        return placeholders;
    }

    /* Regex */
    public static String findIteration(String string, String delimLeft, String delimRIght) {
        Pattern p = Pattern.compile(delimLeft + "(.*?)" + delimRIght);
        Matcher m = p.matcher(string);
        return m.group();
    }


    public static String getStackTrace(Exception ex) {
        StringBuffer sb = new StringBuffer(500);
        StackTraceElement[] st = ex.getStackTrace();
        sb.append(ex.getClass().getName() + ": " + ex.getMessage() + "\n");
        for (int i = 0; i < st.length; i++) {
            sb.append("\t at " + st[i].toString() + "\n");
        }
        return sb.toString();
    }

    public List<DocuLibrary> uploadAndSaveFiles(List<MultipartFile> multipartFiles, String directoryString,
                                                String filePath, String codeRefType, String codeRefId)
            throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String timeStamp = new SimpleDateFormat(SYSTEM_DATE_TIME_FORMAT).format(new Date());
            String uri = storageService.storeInContainer(multipartFile, appProfile, directoryString
                            + getCompKey() + filePath,
                    multipartFile.getOriginalFilename() + "-" + timeStamp, getCompKey()
                    , true);
            DocuLibrary docuLibrary = saveOrUpdateDocuLibrary(DocuLibrary.builder()
                    .docuName(multipartFile.getOriginalFilename())
                    .uri(uri)
                    .docuType(multipartFile.getContentType())
                    .visibilityKey(true)
                    .codeRefType(codeRefType)
                    .codeRefId(codeRefId)
                    .build());
            docuLibraryList.add(docuLibrary);
        }
        return docuLibraryList;
    }

    private DocuLibrary saveOrUpdateDocuLibrary(DocuLibrary docuLibrary) {
        if (docuLibrary.getDocuId() != null) {
            DocuLibrary docuLibraryData = docuLibraryRepository.findById(docuLibrary.getDocuId())
                    .orElseThrow(() -> new NotFoundException(DocuLibrary.class, docuLibrary.getDocuId()));
            if (docuLibraryData == null) {
                throw new NotFoundException(CompanyPreference.class, docuLibrary.getDocuId());
            }
            docuLibraryData = DocuMapper.toUpdatedDocuLibrary(docuLibraryData,
                    docuLibrary);
            return docuLibraryRepository.save(docuLibraryData);
        }
        return docuLibraryRepository.save(docuLibrary);
    }

    public static List<String> getBarXAxisLabelsForMonths() throws ParseException {
        List<String> monthsList = Arrays.asList(Month.values()).stream().map(m -> m.getValue() < 10 ? "0" + m.getValue() : String.valueOf(m.getValue())).collect(Collectors.toList());
        return monthsList;
    }

    public static String getMonthFromDate(String date) {
        return date.substring(0, date.indexOf("-"));
    }

    public static String getMaskedString(String value) {
        StringBuffer temp = new StringBuffer();
        int i = 0;
        int tillLength = 0;
        if (value.length() > 7) {
            tillLength = value.length() - 4;
        } else {
            tillLength = value.length() - 2;
        }
        while (i < (value.length())) {
            if (i >= tillLength) {
                temp.append(value.charAt(i));
            } else {
                temp.append("*");
            }
            i++;
        }
        return temp.toString();
    }

    public static boolean isAfter(Date date1, Date date2) {
        return new DateTime(date1).isAfter(new DateTime(date2));
    }

    /**
     * @param v1 = Current Record
     * @param v2 = Last Record
     * @param d  = CONSTANT
     * @return
     */
    public static Double subAndDiv(BigDecimal v1, BigDecimal v2, int d) {
        return v1.subtract(v2).divide(BigDecimal.valueOf(d), 2, RoundingMode.DOWN).doubleValue();
    }

    public static NodeList toXPath(Document d, String exp) throws XPathExpressionException {
        return (NodeList) XPathFactory.newInstance().newXPath().compile(exp).evaluate(d, XPathConstants.NODESET);
    }

    public static int getIndex(String s, List<String> list) {
        int index = Iterables.indexOf(list, l -> s.equalsIgnoreCase(l.toString()));
        return index < list.size() ? index : -1;
    }

    public static List<java.time.LocalDate> getDateList(int year, String monthName) {
        int month = Month.valueOf(monthName.toUpperCase()).getValue();
        return IntStream.rangeClosed(1, YearMonth.of(year, month).lengthOfMonth())
                .mapToObj(i -> java.time.LocalDate.of(year, month, i))
                .collect(Collectors.toList());
    }

    public static List<java.time.LocalDate> getDateListBetween(String fromDate, String toDate) {
        java.time.LocalDate start = java.time.LocalDate.parse(fromDate);
        java.time.LocalDate end = java.time.LocalDate.parse(toDate);
        List<java.time.LocalDate> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start);
            start = start.plusDays(1);
        }
        return totalDates;
    }

    public static String toStringFromLocalDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(SYSTEM_DATE_TIME_FORMAT));
    }

    public static LocalDateTime toLocalDateInGMT(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(Utility.DATE_TIME_FORMAT)).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static LocalDateTime toLocalDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(Utility.DATE_TIME_FORMAT)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return getDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()), SYSTEM_DATE_TIME_FORMAT);
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime, String zoneId) {
        return getDate(Date.from(dateTime.atZone(ZoneId.of(zoneId)).toInstant()), SYSTEM_DATE_TIME_FORMAT);
    }

    public static Date fromGMT(Date date) {
        TimeZone tz = TimeZone.getDefault();
        Date dateGMT = new Date(date.getTime() + tz.getRawOffset());

        if (tz.inDaylightTime(dateGMT)) {
            Date dstDate = new Date(dateGMT.getTime() + tz.getDSTSavings());
            if (tz.inDaylightTime(dstDate)) {
                dateGMT = dstDate;
            }
        }
        return dateGMT;
    }

    public static Date toGMT(Date date) {
        TimeZone tz = TimeZone.getDefault();
        Date dateGMT = new Date(date.getTime() - tz.getRawOffset());

        if (tz.inDaylightTime(dateGMT)) {
            Date dstDate = new Date(dateGMT.getTime() - tz.getDSTSavings());
            if (tz.inDaylightTime(dstDate)) {
                dateGMT = dstDate;
            }
        }
        return dateGMT;
    }

    public static Date toEST(Date date) {
        TimeZone tz = TimeZone.getTimeZone("US/Eastern");
        Date dateEst = new Date(date.getTime() + tz.getRawOffset());

        if (tz.inDaylightTime(dateEst)) {
            Date dstDate = new Date(dateEst.getTime() - tz.getDSTSavings());
            if (tz.inDaylightTime(dstDate)) {
                dateEst = dstDate;
            }
        }
        return dateEst;
    }

    public static java.time.LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static org.joda.time.LocalDate toLocalDateJodaTime(Date dateToConvert) {
        return new LocalDate(dateToConvert);
    }

    public static LocalDateTime toLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    //TODO: Add daylight savings
    public static LocalDateTime toLocalDateTimeEastUs(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime datetime) {
        return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime datetime, String zoneId) {
        return Date.from(datetime.atZone(ZoneId.of(zoneId)).toInstant());
    }

    /**
     * convert "yyyy-MM-dd'T'HH:mm:ss" to Date
     *
     * @param dateTime
     * @return
     */
    public static Date toDate(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            return format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date toDateWithFormat(String dateTime, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        try {
            return simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime getUTCForZone(Date date, SimpleDateFormat dateFormat, DateTimeFormatter formatter,
                                              String zone) {
        return getUTCForZone(dateFormat.format(date), formatter, zone);
    }

    public static LocalDateTime getDateFromZoneToZone(String dateString, DateTimeFormatter formatter, String zone1, String zone2) {
        return getDateFromZoneToZone(LocalDateTime.parse(dateString, formatter), zone1, zone2);
    }

    public static LocalDateTime getDateFromZoneToZone(LocalDateTime localDateTime, String zone1, String zone2) {
        return localDateTime.atZone(ZoneId.of(zone1))
                .withZoneSameInstant(ZoneId.of(zone2)).toLocalDateTime();
    }

    public static LocalDateTime getUTCForZone(String dateString, DateTimeFormatter formatter, String zone) {
        return LocalDateTime.parse(dateString, formatter).atZone(ZoneId.of(zone))
                .withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static LocalDateTime getZonedLocalDateTime(Date date, String zoneString) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.of(zoneString);
        return instant.atZone(zone).toLocalDateTime();
    }

    public static Date getZonedDate(Date date, String zoneString) {
        return localDateTimeToDate(getZonedLocalDateTime(date, zoneString));
    }

    public static ZonedDateTime getZonedDateTime(Date date, String zone) {
        return date.toInstant().atZone(ZoneId.of(zone));
    }

    public static String getMeasureAsJson(String stringMeasure, String rateCode) {
        JSONObject jsonObject = new JSONObject(stringMeasure);

        if (jsonObject.has(rateCode)) {
            return jsonObject.getString(rateCode);
        } else {
            return "";
        }
    }

    public static BigDecimal getDifferenceOfIndexValues(List<Double> numbers) {
        double difference = 0.0;
        for (int i = 1; i < numbers.size(); i++) {
            difference = numbers.get(i) - numbers.get(i - 1);
        }
        return BigDecimal.valueOf(difference);
    }

    public static Double subtract(double v1, double v2) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).doubleValue();
    }

    public static Double divide(BigDecimal v1, int d) {
        return v1.divide(BigDecimal.valueOf(d), 2, RoundingMode.DOWN).doubleValue();
    }

    public static Date getComingSaturday() {
        TemporalAdjuster temporalAdjuster = TemporalAdjusters.next(DayOfWeek.SATURDAY);
        java.time.LocalDate date = java.time.LocalDate.now(ZoneId.of("UTC"));
        java.time.LocalDate result = date.with(temporalAdjuster);
        return Date.from(result.atStartOfDay(ZoneId.of("UTC")).toInstant());
    }

    public static Date getTodayUTC() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        return Date.from(nowUTC.toInstant());
    }

    public static Date getNextSaturday(LocalDateTime localDateTime) {
        TemporalAdjuster temporalAdjuster = TemporalAdjusters.next(DayOfWeek.SATURDAY);
        java.time.LocalDate date = localDateTime.toLocalDate();
        java.time.LocalDate result = date.with(temporalAdjuster);
        return Date.from(result.atStartOfDay(ZoneId.of("UTC")).toInstant());
    }


    public static Map generateResponseMap(Map response, String code, String message, Object data) {

        response.put("data", data);
        response.put("message", message);
        response.put("code", code);
        return response;
    }

    public static String getFullMonthYear(int month, int year) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1); // month value starts from 0
        calendar.set(Calendar.YEAR, year);
        return dateFormat.format(calendar.getTime());
    }

    public static String getLastMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MONTH_YEAR_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date lastMonthDate = calendar.getTime();
        return simpleDateFormat.format(lastMonthDate);
    }

    public static String getPreviousYearMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date lastMonthDate = calendar.getTime();
        return simpleDateFormat.format(lastMonthDate);
    }

    public static String convertToShortMonthYearFormat(String monthYear) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat(MONTH_YEAR_FORMAT);
            Date date = originalFormat.parse(monthYear);

            SimpleDateFormat targetFormat = new SimpleDateFormat(MONTH_YEAR_SHORT_FORMAT);
            return targetFormat.format(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected MM-yyyy");
        }
    }

    /**
     * @param startTime -- start time
     * @param endTime   -- end time
     * @param interval  -- interval to add between timestamps
     * @return -- Date ArrayList
     * @author Shariq
     * <p>
     * Built for Inverter(s)
     * Generate TimeStamps between two dates
     * With given interval
     */
    public static List<Date> getTimeStamps(Date startTime, Date endTime, int interval) {
        if (endTime.after(new Date())) {
            LOGGER.info("Requested Time {} is after the current time {}", endTime, new Date());
            return new ArrayList<>();
        }
        List<Date> dateList = new ArrayList<>();
        while (!startTime.equals(endTime)) {
            dateList.add(startTime);
            startTime = Utility.addMinutes(startTime, interval);
        }
        return dateList;
    }

    /**
     * @param dateList
     * @return List<LocalDateTime>
     * @author Shariq
     * Built for TIGO Inverter
     * Converts date to EST timezone
     */
    public static List<LocalDateTime> getTimeStampsInLocalDate(List<Date> dateList) {
        List<LocalDateTime> localDates = new ArrayList<>();
        dateList.forEach(d -> {
            localDates.add(Utility.toLocalDateTimeEastUs(d));
        });
        return localDates;
    }

    public static String getFormattedDate(LocalDateTime localDateTime) {
        // Input LocalDateTime in "yyyy-MM-dd'T'HH:mm" format

        // Create a DateTimeFormatter for the input format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        // Parse the inputDateTime string to LocalDateTime
        LocalDateTime localDateTime1 = LocalDateTime.parse(String.valueOf(localDateTime), inputFormatter);

        // Create a DateTimeFormatter for the output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // Format the LocalDateTime to a string in the desired format
        return localDateTime1.format(outputFormatter);
    }

    public static Date getEndOfDayUTC() {
        java.time.ZonedDateTime zdt = java.time.ZonedDateTime.now(ZoneId.of("UTC")).with(LocalTime.MAX).minusSeconds(1);
        return Date.from(zdt.toInstant());
    }

    public static List<Year> getYearsInRange(int startYear, int endYear) {
        return IntStream.rangeClosed(startYear, endYear)
                .mapToObj(Year::of)
                .collect(Collectors.toList());
    }

    public ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatus status, String message) {
        ErrorDTO errorResponse = new ErrorDTO(status.value(), message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    public static List<String> getQuartersForYear(int year) {
        List<String> quarters = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Q'Q-yyyy");

        for (int quarterNumber = 1; quarterNumber <= 4; quarterNumber++) {
            String quarter = Year.of(year).atMonth(Month.of(quarterNumber * 3)).format(formatter);
            quarters.add(quarter);
        }

        return quarters;
    }

    public static List<String> getMonthsForYear(int year) {
        List<String> months = new ArrayList<>();
        for (int monthNumber = 1; monthNumber <= 12; monthNumber++) {
            String formattedMonth = String.format("%02d-%d", monthNumber, year);
            months.add(formattedMonth);
        }

        return months;
    }

    public static List<WeekInfoDTO> getWeeksList(String yearMonth) {
        List<WeekInfoDTO> weeksList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(yearMonth));
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        } catch (Exception e) {
            e.printStackTrace();
        }

        int weekNumber = 1;
        int currentMonth = calendar.get(Calendar.MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int lastWeekDays = daysInMonth % 7; // Days remaining after full weeks

        while (calendar.get(Calendar.MONTH) == currentMonth) {
            WeekInfoDTO weekInfo = WeekInfoDTO.builder().build();
            weekInfo.setWeekNumber(weekNumber);
            weekInfo.setStartDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            // Calculate the end of the week (last Sunday)
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                    calendar.get(Calendar.MONTH) == currentMonth) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            int endDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (weekNumber == (daysInMonth / 7) + 1) {
                endDay = daysInMonth; // Set last week's end day to the last day of the month
            }

            //  if (weekNumber == (daysInMonth / 7) && lastWeekDays > 0) {
            //     endDay = daysInMonth; // Adjust last week's end day to the last day of the month
            //   }
            // Move to the next week
            weekNumber++;
            weekInfo.setEndDay(String.valueOf(endDay));
            weekInfo.setMonthName(getMonthName(currentMonth + 1));
            weeksList.add(weekInfo);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }
        return weeksList;
    }

    public static List<String> getMonthDays(int year, int month) {
        List<String> daysList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date date = calendar.getTime();
            String formattedDate = sdf.format(date);
            daysList.add(formattedDate);
        }

        return daysList;
    }

    public static String getMonthName(int monthNumber) {
        Month month = Month.of(monthNumber);
        // Get the three-letter abbreviation of the month
        String monthAbbreviation = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return monthAbbreviation;
    }

    public String getInverterNumber(ExtDataStageDefinition ext) {
        String inverterNumber;
        if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLRENVEIW)) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SVDID);
        } else if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_EGAUGE)) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.EGDID);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAX)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLIS)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.S_PN);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_TIGO))) {
            inverterNumber = checkForInverter(ext.getMpJson());
        } else {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        }
        return inverterNumber;
    }

    private String checkForInverter(String mpJson) {
        String inverterNo = Utility.getMeasureAsJson(mpJson, Constants.RATE_CODES.DEVICE_NUMBER);
        if (inverterNo == null || inverterNo.isBlank() || inverterNo.equals("-1")) {
            inverterNo = Utility.getMeasureAsJson(mpJson, Constants.RATE_CODES.SITEID);
        }
        return inverterNo;
    }

    public static List<String> getNextThreeMonths() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MONTH_YEAR_FORMAT);
        Calendar calendar = Calendar.getInstance();
        List<String> nextThreeMonths = new ArrayList<>();
        Date currentDate = calendar.getTime();
        nextThreeMonths.add(simpleDateFormat.format(currentDate));
        for (int i = 1; i <= 2; i++) {
            calendar.add(Calendar.MONTH, 1); // increment the month by 1
            Date nextMonthDate = calendar.getTime();
            nextThreeMonths.add(simpleDateFormat.format(nextMonthDate));
        }
        return nextThreeMonths;
    }


    //for dates
    public static List<String> generateDates(Integer x) {
        List<String> dates = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SYSTEM_DATE_FORMAT).withZone(ZoneId.of("UTC"));

        String todayDate = formatter.format(today);
        dates.add(todayDate);

        java.time.LocalDate futureDate = today.plusDays(x);
        String futureFormattedDate = formatter.format(futureDate);
        dates.add(futureFormattedDate);

        return dates;
    }

    public static String getCurrentMonthLikeQuery() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        Calendar calendar = Calendar.getInstance();
        Date lastMonthDate = calendar.getTime();
        return simpleDateFormat.format(lastMonthDate);
    }

    public static String getLastMonthLikeQuery() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date lastMonthDate = calendar.getTime();
        return simpleDateFormat.format(lastMonthDate);
    }

    public static String getCurrentMonthName() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("MMM").format(cal.getTime());
    }

    public static String formatUTCDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MON_DAY_YEAR_H_MM_A);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String getCurrentMonth() {
        YearMonth yearMonth = YearMonth.now();
        return yearMonth.format(DateTimeFormatter.ofPattern(CAPITAL_MONTH_YEAR_FORMAT, Locale.ENGLISH));
    }

    public static String getCurrentMonthYear() {
        YearMonth yearMonth = YearMonth.now();
        return yearMonth.format(DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT, Locale.ENGLISH));
    }

    public static List<YearMonth> getLastYearMonthDuring(int months) {
        List<YearMonth> monthYearRange = new ArrayList<>();
        if (months == 0) {
            return monthYearRange;
        }
        YearMonth yearMonth = YearMonth.now().minusMonths(months);
        monthYearRange.add(yearMonth);
        for (int i = 1; i < months; i++) {
            yearMonth = yearMonth.plusMonths(1);
            monthYearRange.add(yearMonth);
        }
        return monthYearRange;
    }

    public static List<String> getCapitalizedNextThreeMonths(List<String> months) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

        List<Date> parsedMonths = parseInputMonths(months, inputDateFormat);

        List<String> nextThreeMonths = generateNextMonths(parsedMonths, outputDateFormat);

        sortMonths(nextThreeMonths, outputDateFormat);

        return nextThreeMonths;
    }

    private static List<Date> parseInputMonths(List<String> months, SimpleDateFormat inputDateFormat) {
        return months.stream()
                .map(month -> parseMonth(month, inputDateFormat))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Date parseMonth(String month, SimpleDateFormat inputDateFormat) {
        try {
            return inputDateFormat.parse(month);
        } catch (Exception e) {
            System.out.println("Invalid input date format: " + month);
            return null;
        }
    }

    private static List<String> generateNextMonths(List<Date> parsedMonths, SimpleDateFormat outputDateFormat) {
        Calendar calendar = Calendar.getInstance();

        List<String> nextThreeMonths = new ArrayList<>(parsedMonths.stream()
                .map(date -> generateMonthName(date, outputDateFormat))
                .collect(Collectors.toList()));

        int remainingMonths = Math.max(0, 3 - nextThreeMonths.size());
        Date lastDate = parsedMonths.isEmpty() ? new Date() : parsedMonths.get(parsedMonths.size() - 1);
        calendar.setTime(lastDate);
        Optional<Integer> maxMonth = getMaxMonth(parsedMonths);

        if (maxMonth.isPresent()) {
            List<String> additionalMonths = generateAdditionalMonths(calendar, outputDateFormat, remainingMonths);

            nextThreeMonths.addAll(additionalMonths);
        }

        return nextThreeMonths;
    }

    private static String generateMonthName(Date date, SimpleDateFormat outputDateFormat) {
        String monthName = outputDateFormat.format(date);
        return Character.toUpperCase(monthName.charAt(0)) + monthName.substring(1);
    }

    private static Optional<Integer> getMaxMonth(List<Date> parsedMonths) {
        return parsedMonths.stream()
                .map(date -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    return cal.get(Calendar.MONTH);
                })
                .max(Comparator.naturalOrder());
    }

    private static List<String> generateAdditionalMonths(Calendar calendar, SimpleDateFormat outputDateFormat, int remainingMonths) {
        return IntStream.range(0, remainingMonths)
                .mapToObj(monthsToAdd -> {
                    calendar.add(Calendar.MONTH, 1);
                    Date nextMonthDate = calendar.getTime();
                    return generateMonthName(nextMonthDate, outputDateFormat);
                })
                .collect(Collectors.toList());
    }

    private static void sortMonths(List<String> months, SimpleDateFormat outputDateFormat) {
        months.sort(Comparator.comparingInt(s -> {
            try {
                Date date = outputDateFormat.parse(s);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                return cal.get(Calendar.MONTH);
            } catch (Exception e) {
                return -1;
            }
        }));
    }

    public static List<String> getDaysOfMonth(int year, int month) {
        List<String> daysList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date date = calendar.getTime();
            String formattedDate = sdf.format(date);
            daysList.add(formattedDate);
        }

        return daysList;
    }

    public static String roundAndFormat(double value, int rounding) {
        double roundedValue = Math.round(value * Math.pow(10, rounding)) / Math.pow(10, rounding);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(roundedValue);
    }
    public static int convertFahrenheitIntoCelsius(int value){
        return (value - 32)  * 5/9;
    }
    public <T> ResponseEntity<SuccessDTO> buildSuccessResponse(HttpStatus status, String message, T data) {
        SuccessDTO<T> successResponse = new SuccessDTO(status.value(), message,data);
        return ResponseEntity.status(status).body(successResponse);
    }
    public static Date convertStringDateInToDate(String dateString) {
        if (dateString != null && dateString.length() > 0) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                return dateFormat.parse(dateString);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Error while converting string date to date: " + dateString);
            }
        }
        return null;
    }

    public static String getCurrentYearMonthAndPreviousDay(){
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        // Subtract one day to get the previous day
        java.time.LocalDate previousDay = currentDate.minusDays(1);
        // Define a custom format pattern for printing
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_FORMAT);
        // Format the current year, month, and previous day using the custom pattern
        String formattedDate = previousDay.format(customFormatter);
        return formattedDate;
    }

    /*Encode passCode using Base64   */
    public String encodePassCode(String passCode) {
        byte[] passCodeBytes = passCode.getBytes();
        String encodedPassCode = Base64.getEncoder().encodeToString(passCodeBytes);
        return encodedPassCode;
    }
    /*  Decode the passCode using Base64*/
    public String decodePassCode(String passCode) {
        byte[] decodedBytes = Base64.getDecoder().decode(passCode);
        return new String(decodedBytes);
    }
    public static String getStringValue(JSONObject obj, String key) {
        return obj.has(key) && !obj.isNull(key) ? obj.getString(key) : null;
    }

    public static Long getLongValue(JSONObject obj, String key) {
        return obj.has(key) && !obj.isNull(key) ? obj.getLong(key) : null;
    }

    public static Double getDoubleValue(JSONObject obj, String key) {
        return obj.has(key) && !obj.isNull(key) ? obj.getDouble(key) : null;
    }
    public static LocalDateTime getDateTimeValue(JSONObject obj, String key) {
        if (obj.has(key) && !obj.isNull(key)) {
            JSONObject dateObject = obj.getJSONObject(key);
            if (dateObject.has("$date") && !dateObject.isNull("$date")) {
                String dateString = dateObject.getString("$date");
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            }
        }
        return null;
    }
    /**
     * @param String input
     * @return String
     * @author Sheharyar
     * Converts string to Sentence Case
     */
    public static String toSentenceCase(String input) {
        // Check if the input is not null or empty
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Convert the first character to uppercase
        char firstChar = Character.toUpperCase(input.charAt(0));

        // Convert the rest of the characters to lowercase
        String restOfString = input.substring(1).toLowerCase();

        // Combine the first character and the rest of the string
        return firstChar + restOfString;
    }
}


