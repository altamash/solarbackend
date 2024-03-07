package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.report.ReportIteratorDefinitionDTO;
import com.solar.api.tenant.mapper.report.ReportTemplateDTO;
import com.solar.api.tenant.model.report.ReportTemplate;
import com.solar.api.tenant.model.report.TrueUp;
import com.solar.api.tenant.service.process.billing.publish.BillingInvoicePublishService;
import com.solar.api.tenant.service.process.reporting.Generator;
import com.solar.api.tenant.service.process.reporting.ReportService;
import com.solar.api.tenant.service.trueup.TrueUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.report.ReportMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ReportController")
@RequestMapping(value = "/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private TrueUpService trueUpService;
    @Autowired
    private Generator generator;
    @Autowired
    private BillingInvoicePublishService publishService;

    // ReportTemplate //////////////////////////////////////////////////////////
    @PostMapping("/reportTemplate")
    public ReportTemplateDTO add(@RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam(value = "templateName") String templateName,
                                 @RequestParam(value = "category") String category,
                                 @RequestParam(value = "type") String type,
                                 @RequestParam(value = "outputFormat") String outputFormat,
                                 @RequestParam(value = "templateURI", required = false) String templateURI,
                                 @RequestParam(value = "permission", required = false) String permission,
                                 @RequestParam(value = "container") String container,
                                 @RequestHeader("Comp-Key") Long compKey) throws URISyntaxException, StorageException
            , IOException {
        return toReportTemplateDTO(reportService.saveOrUpdate(file, ReportTemplate.builder()
                .templateName(templateName)
                .category(category)
                .type(type)
                .outputFormat(outputFormat)
                .templateURI(templateURI)
                .permission(permission)
                .build(), container, compKey));
    }

    @PutMapping("/reportTemplate")
    public ReportTemplateDTO update(@RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam(value = "id") Long id,
                                    @RequestParam(value = "templateName", required = false) String templateName,
                                    @RequestParam(value = "category", required = false) String category,
                                    @RequestParam(value = "type", required = false) String type,
                                    @RequestParam(value = "outputFormat", required = false) String outputFormat,
                                    @RequestParam(value = "templateURI", required = false) String templateURI,
                                    @RequestParam(value = "permission", required = false) String permission,
                                    @RequestParam(value = "permission") String container,
                                    @RequestHeader("Comp-Key") Long compKey) throws URISyntaxException,
            StorageException, IOException {
        return toReportTemplateDTO(reportService.saveOrUpdate(file, ReportTemplate.builder()
                .id(id)
                .templateName(templateName)
                .category(category)
                .type(type)
                .outputFormat(outputFormat)
                .templateURI(templateURI)
                .permission(permission)
                .build(), container, compKey));
    }

    @GetMapping("/reportTemplate/{id}")
    public ReportTemplateDTO findById(@PathVariable Long id) {
        return toReportTemplateDTO(reportService.findById(id));
    }

    @GetMapping("/reportTemplate/{templateName}/{outputFormat}")
    public ReportTemplateDTO findByTemplateNameAndOutputFormat(@PathVariable String templateName,
                                                               @PathVariable String outputFormat) {
        return toReportTemplateDTO(reportService.findByTemplateNameAndOutputFormat(templateName, outputFormat));
    }

    @GetMapping("/reportTemplate")
    public List<ReportTemplateDTO> findAll() {
        return toReportTemplateDTOs(reportService.findAll());
    }

    @DeleteMapping("/reportTemplate/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        reportService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/reportTemplate")
    public ResponseEntity deleteAll() {
        reportService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ReportIteratorDefinition //////////////////////////////////////////////////////////
    @PostMapping("/reportIteratorDefinition")
    public ReportIteratorDefinitionDTO addReportIteratorDefinition(@RequestBody ReportIteratorDefinitionDTO definitionDTO) {
        return toReportIteratorDefinitionDTO(reportService.saveOrUpdateReportIteratorDefinition(toReportIteratorDefinition(definitionDTO)));
    }

    @PutMapping("/reportIteratorDefinition")
    public ReportIteratorDefinitionDTO updateReportIteratorDefinition(@RequestBody ReportIteratorDefinitionDTO definitionDTO) {
        return toReportIteratorDefinitionDTO(reportService.saveOrUpdateReportIteratorDefinition(toReportIteratorDefinition(definitionDTO)));
    }

    @GetMapping("/reportIteratorDefinition/{id}")
    public ReportIteratorDefinitionDTO findReportIteratorDefinitionById(@PathVariable Long id) {
        return toReportIteratorDefinitionDTO(reportService.findReportIteratorDefinitionById(id));
    }

    @GetMapping("/reportIteratorDefinition")
    public List<ReportIteratorDefinitionDTO> findAllReportIteratorDefinitions() {
        return toReportIteratorDefinitionDTOs(reportService.findAllReportIteratorDefinitions());
    }

    @DeleteMapping("/reportIteratorDefinition/{id}")
    public ResponseEntity deleteReportIteratorDefinitionById(@PathVariable Long id) {
        reportService.deleteReportIteratorDefinition(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/reportIteratorDefinition")
    public ResponseEntity deleteAllReportIteratorDefinition() {
        reportService.deleteAllReportIteratorDefinition();
        return new ResponseEntity(HttpStatus.OK);
    }
    /////////////////////

//    @Autowired
//    private TemplateEngine templateEngine;

    @GetMapping("/generatePDF/{billHeadId}")
    public void generatePDF(@PathVariable Long billHeadId, @RequestHeader("Comp-Key") Long compKey) throws Exception {
        generator.generatePDF(billHeadId, compKey);
    }

    public static void main(String[] args) throws Exception {
    }

//    private void generate(Long billHeadId) throws Exception {
//        Context context = new Context();
//        context.setVariable("name", "Test");
//        String template = IOUtils.toString(ReportController.class.getResourceAsStream("/template/invoice.xhtml"),
//        String.valueOf(StandardCharsets.UTF_8));
//        String html = new SpringTemplateEngine().process(template, context);
//        try (OutputStream os = new FileOutputStream
//        ("C:/workspace/Client/Salman/SolarInformatics/solarbackend/src/main/resources/template/invoice.pdf")) {
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withHtmlContent(html,
//                    ReportController.class.getResource("/template/root.htm")
//                            .toExternalForm()
//                                   );
//
//            builder.useFastMode();
//            builder.toStream(os);
//            builder.run();
//        }
//    }

    /**
     * TODO: Ad HOC Batch in future (Bulk)
     * Send Emails to INVOICED users
     * @throws Exception
     */
//    @GetMapping("/sendBulkEmails")
//    public ObjectNode sendBulkEmails() throws Exception {
//        return emailService.sendEmails();
//    }

//    @PostMapping("/invoicingBySubscriptionType/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth
//    }/{type}")
//    public ObjectNode invoicingBySubscriptionType(@PathVariable("subscriptionCode") String subscriptionCode,
//                                    @PathVariable(value = "subscriptionRateMatrixIdsCSV", required = false) String
//                                    subscriptionRateMatrixIdsCSV,
//                                    @PathVariable("billingMonth") String billingMonth,
//                                    @PathVariable("type") String type,
//                                    @RequestParam(value = "date", required = false) String dateString) throws
//                                    Exception {
//        List<Long> subscriptionRateMatrixIds =  Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id ->
//        Long.parseLong(id)).collect(Collectors.toList());
//        Date date = null;
//        if (dateString != null) {
//            String[] monthYear = billingMonth.split("-");
//            dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
//            date = dateString != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
//        }
//        String message = publishService.publishInvoiceByMonth(subscriptionCode, subscriptionRateMatrixIds,
//        billingMonth, date, type);
//        ObjectNode messageJson = new ObjectMapper().createObjectNode();
//        messageJson.put("message", "Your billing job request is successfully submitted. It may take several minutes
//        to execute depending on data.");
//        return messageJson;
//    }

    /**
     * @param billHeadId
     * @return
     */
    @GetMapping("/publishInvoice/{billHeadId}")
    public ObjectNode publishInvoice(@PathVariable Long billHeadId) {
        return publishService.publishIndividualInvoice(billHeadId);
    }

    @GetMapping("/trueUps/getAllByGarden/{subscriptionRateMatrixId}/{subscriptionId}")
    public Object getAllTrueUps(@PathVariable Long subscriptionRateMatrixId, @PathVariable Long subscriptionId) {
        List<TrueUp> trueUps = new ArrayList<>();
        if (subscriptionId == -1) {
            List<TrueUp> trueUpList = trueUpService.getAllByGardenId(subscriptionRateMatrixId);
            if (trueUpList.isEmpty()) {
                ObjectNode objectNode = new ObjectMapper().createObjectNode();
                objectNode.put("message", "No true ups generated for this Garden");
                return objectNode;
            } else {
                trueUps = trueUpList;
            }
        } else {
            TrueUp trueUp = trueUpService.getBySubscriptionId(subscriptionId);
            if (trueUp == null) {
                ObjectNode objectNode = new ObjectMapper().createObjectNode();
                objectNode.put("message", "No true ups generated for this Subscription");
                return objectNode;
            } else {
                trueUps.add(trueUp);
            }
        }
        return trueUps;
    }

//    @GetMapping("/trueUps/getOneBySubscription/{subscriptionId}")
//    public TrueUp getOneBySubscription(@PathVariable Long subscriptionId) {
//        return trueUpService.getBySubscriptionId(subscriptionId);
//    }

    @GetMapping("/trueUps/getTrueUpUrl/{subscriptionId}")
    public ObjectNode getTrueUp(@PathVariable Long subscriptionId) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        TrueUp trueUp = trueUpService.getBySubscriptionId(subscriptionId);
        objectNode.put("ReportUrl", trueUp.getReportUrl());
        return objectNode;
    }

    //BulkEmailByGardenId
    @GetMapping("/trueUps/emailByGardenId/{type}/{subscriptionRateMatrixId}")
    public ObjectNode emailByGardenId(@PathVariable String type, @PathVariable Long subscriptionRateMatrixId) throws Exception {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        List<TrueUp> trueUps = trueUpService.getAllByGardenId(subscriptionRateMatrixId);
        if (!trueUps.isEmpty()) {
            trueUpService.emailBySubscriptionRateMatrixId(type, subscriptionRateMatrixId);
            objectNode.put("message", "Emails by Garden id: " + subscriptionRateMatrixId + " is in progress");
        } else {
            objectNode.put("error", "TrueUps not found for Garden id: " + subscriptionRateMatrixId);
        }
        return objectNode;
    }

    //EmailBySubscriptionId
    @GetMapping("/trueUps/emailBySubscriptionId/{type}/{subscriptionId}")
    public ObjectNode emailBySubscriptionId(@PathVariable String type, @PathVariable Long subscriptionId) throws Exception {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        TrueUp trueUp = trueUpService.getBySubscriptionId(subscriptionId);
        if (trueUp != null) {
            trueUp = trueUpService.emailBySubscriptionId(type, subscriptionId);
            objectNode.put("message", "Email sent to user: " + trueUp.getAcctId());
        } else {
            objectNode.put("error", "TrueUps not found for Subscription id: " + subscriptionId);
        }
        return objectNode;
    }

    @DeleteMapping("/trueUps/deleteByGardenId/{subscriptionRateMatrixId}")
    public ResponseEntity deleteByGardenId(@PathVariable Long subscriptionRateMatrixId) {
        trueUpService.deleteByGarden(subscriptionRateMatrixId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
