package com.solar.api.helper;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVHelper.class);

    public static String TYPE = "text/csv";

    static String[] HEADERs = {
            "Payment Type",
            "Debtor Number",
            "Premise Number",
            "Subscriber Allocation History: Subscriber Name",
            "Monthly Production Allocation in kWh",
            "Tariff Rate",
            "Bill Credit",
            "Garden ID",
            "Name Plate Capacity kW DC",
            "Calendar Month"
    };

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<BillingCredits> csvToBillingCredits(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim().withQuote(null))) {
            System.out.println(csvParser);

            List<BillingCredits> billingCredits = new ArrayList<BillingCredits>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            return billingCredits;
        } catch (IOException e) {
            LOGGER.error("fail to parse CSV file: ", e.getMessage(), e);
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

}
