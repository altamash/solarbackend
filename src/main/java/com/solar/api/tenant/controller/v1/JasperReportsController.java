package com.solar.api.tenant.controller.v1;

import com.solar.api.AppConstants;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.service.BillingHeadService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("JasperReportsController")
@RequestMapping(value = "/JasperReports")
public class JasperReportsController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final ModelAndView model = new ModelAndView();

    @Autowired
    private BillingHeadService billingHeadService;

    // Method to display the index page of the application.
    @GetMapping(value = "/welcome")
    public ModelAndView index() {
        LOGGER.info("Showing the welcome page.");
        model.setViewName("welcome");
        return model;
    }

    @GetMapping(value = "/view/jasperReport/{id}")
    public String JasperPDF(@PathVariable Long id) throws JRException, SQLException {

        //JDBC Connection
        try (Connection connection = DriverManager.getConnection(AppConstants.API_DATABASE_PREFIX + "localhost:3306/ec1001", "root",
                "root")) {

            //Compiling
            JasperReport jasperReport = JasperCompileManager.compileReport("" +
                    "C:\\Users\\Al Shaikh\\JaspersoftWorkspace\\InvoiceReports\\main_invoice.jrxml");
            System.out.println("Done Compiling..");

            //Filling
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);
            System.out.println("Done Filling..");

            //Exporting
            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\Al Shaikh\\Desktop\\Invoice_report.pdf");
            System.out.println("Done Export..");
        }

        return "Downloaded";
    }
    // Method to create the pdf report via jasper framework.

    @GetMapping(value = "/view/{id}")
    public String viewReport(@PathVariable Long id) {
        LOGGER.info("Preparing the pdf report via jasper.");
        try {
            createPdfReport(billingHeadService.findById(id));
            LOGGER.info("File successfully saved at the given path.");
        } catch (final Exception e) {
            LOGGER.error("Some error has occurred while preparing the employee pdf report.", e);
        }
        return "Invoice Generated Successfully!";
    }

    // Method to create the pdf file using the employee list datasource.
    private void createPdfReport(final BillingHead billingHeads) throws JRException {
        // Fetching the .jrxml file from the resources folder.
        final InputStream stream = this.getClass().getResourceAsStream("/invoice.jrxml");
        List<Address> billingAddress = billingHeads.getUserAccount().getAddresses().stream().filter(s ->
                "Mailing".equalsIgnoreCase(s.getAddressType())).collect(Collectors.toList());
        List<Address> shippingAddress = billingHeads.getUserAccount().getAddresses().stream().filter(s ->
                "Site".equalsIgnoreCase(s.getAddressType())).collect(Collectors.toList());
//        if(!billingAddress.isEmpty()){
//            billingAddress.get(0);
//        }

        // Compile the Jasper report from .jrxml to .japser
        final JasperReport report = JasperCompileManager.compileReport(stream);

        // Fetching the employees from the data source.
        final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(Arrays.asList(billingHeads));

        // Adding the additional parameters to the pdf.
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("SUB_INVOICE", "SUB_INVOICE");
        parameters.put("first_name", billingHeads.getUserAccount().getFirstName());
        parameters.put("last_name", billingHeads.getUserAccount().getLastName());
        parameters.put("ba_street", billingAddress.isEmpty() ? " " : billingAddress.get(0).getAddress1());
        parameters.put("ba_state", billingAddress.isEmpty() ? " " : billingAddress.get(0).getState());
        parameters.put("ba_city", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCity());
        parameters.put("ba_country_code", billingAddress.isEmpty() ? " " : billingAddress.get(0).getCountryCode());
        parameters.put("ba_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());
        parameters.put("sa_street", shippingAddress.isEmpty() ? " " : shippingAddress.get(0).getAddress1());
        parameters.put("sa_state", shippingAddress.isEmpty() ? " " : shippingAddress.get(0).getState());
        parameters.put("sa_city", shippingAddress.isEmpty() ? " " : shippingAddress.get(0).getCity());
        parameters.put("sa_country_code", shippingAddress.isEmpty() ? " " : shippingAddress.get(0).getCountryCode());
        parameters.put("sa_postal", billingAddress.isEmpty() ? " " : billingAddress.get(0).getPostalCode());
        parameters.put("customer_number", billingHeads.getUserAccount().getAcctId());
        parameters.put("customer_reference", "reference");
        parameters.put("invoice_number", billingHeads.getId());
        parameters.put("invoice_date", "date");
        parameters.put("due_date", "date");
        parameters.put("disc_date", "disc_date");
        parameters.put("terms", "Due upon Receipt");
        parameters.put("JasperMainReportDataSource", source);
        parameters.put("JasperCustomSubReportDataSource", source);

        // Filling the report with the employee data and additional parameters information.
        final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

        // Users can change as per their project requirements or can take it as request input requirement.
        // For simplicity, this tutorial will automatically place the file under the "c:" drive.
        // If users want to download the pdf file on the browser, then they need to use the "Content-Disposition"
        // technique.
        final String filePath = "C:\\Users\\Al Shaikh\\Desktop\\";
        // Export the report to a PDF file.
        JasperExportManager.exportReportToPdfFile(print, filePath + "Invoice_report0.pdf");
    }
}
