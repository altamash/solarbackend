package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoDTO;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoWrapper;
import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.paymentDetailView.PaymentDetailReturnObject;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.service.PaymentInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.solar.api.tenant.mapper.payment.info.PaymentInfoMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PaymentInfoController")
@RequestMapping(value = "/paymentInfo")
public class PaymentInfoController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaymentInfoService paymentInfoService;

    @PostMapping
    public PaymentInfoDTO add(@RequestBody PaymentInfoDTO paymentInfoDTO) {
        return toPaymentInfoDTO(paymentInfoService.addOrUpdate(toPaymentInfo(paymentInfoDTO)));
    }

    @PutMapping
    public PaymentInfoDTO update(@RequestBody PaymentInfoDTO paymentInfoDTO) {
        return toPaymentInfoDTO(paymentInfoService.addOrUpdate(toPaymentInfo(paymentInfoDTO)));
    }

    @GetMapping("/{id}")
    public PaymentInfoDTO findById(@PathVariable Long id) {
        return toPaymentInfoDTO(paymentInfoService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public List<PaymentInfoDTO> findByUserId(@PathVariable Long userId) {
        return toPaymentInfoDTOs(paymentInfoService.findByUserId(userId));
    }


    @GetMapping("/decode/{paymentInfoId}")
    public PaymentInfoDTO findDecodedByUserId(@PathVariable Long paymentInfoId) {
        return toPaymentInfoDecodedDTO(paymentInfoService.findById(paymentInfoId));
    }

    @GetMapping("/exportInfo/{userId}")
    public @ResponseBody
    Object getFileV1(HttpServletRequest request, HttpServletResponse response, @PathVariable Long userId) {
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=PaymentInfoArchive.zip");

            List<PaymentInfo> paymentInfoList = paymentInfoService.findByUserId(userId);

            PrintWriter writer1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("PaymentInformation" +
                    ".csv"), "UTF-8"));
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer1, CsvPreference.EXCEL_PREFERENCE);
            String[] csvHeader = {
                    "Id", "Portal Account Id", "Account Number", "Account Title", "Account Type", "Bank Name", "EC " +
                    "Approved", "Payment Source", "Payment SRC Alias",
                    "Primary Indicator", "Routing Number", "Sequence Number", "Created at", "Updated at"
            };

            csvWriter.writeHeader(csvHeader);
            for (PaymentInfo data : paymentInfoList) {
                String[] nameMapping = {
                        "id", "portalAccount", "accountNumber", "accountTitle", "accountType", "bankName",
                        "ecApproved", "paymentSource", "paymentSrcAlias",
                        "primaryIndicator", "routingNumber", "sequenceNumber", "createdAt", "updatedAt"
                };
                try {
                    csvWriter.write(data, nameMapping);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            writer1.print(csvWriter);
            writer1.flush();
            csvWriter.flush();

            File file1 = new File("PaymentInformation.csv");
            filesToZip(response, file1);
            file1.delete();
            response.flushBuffer();
            return null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return e;
        }
    }

    public static void filesToZip(HttpServletResponse response, File... files) throws IOException {
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        // create the ZIP file
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        // compress the files
        for (int i = 0; i < files.length; i++) {
            FileInputStream in = new FileInputStream(files[i].getName());

            // add ZIP entry to output stream
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // complete the entry
            out.closeEntry();
            in.close();
        }
        // complete the ZIP file
        out.close();
    }

    @GetMapping("/gardenId/{gardenId}/month/{month}/paymentSource/{paymentSource}/billStatus/{billStatus}")
    public PaymentInfoWrapper getPaymentInfoByGardenId(@PathVariable String gardenId,
                                                       @PathVariable String month,
                                                       @PathVariable String paymentSource,
                                                       @PathVariable String billStatus) {
        return paymentInfoService.getPaymentInfoByGardenId(gardenId, month, paymentSource, billStatus);
    }

    @GetMapping
    public List<PaymentInfoDTO> findAll() {
        return toPaymentInfoDTOs(paymentInfoService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        paymentInfoService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        paymentInfoService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/searchBy")
    public Object search(@RequestBody SearchParams searchParams) {
        List<PaymentDetailsView> paymentDetailsViews = paymentInfoService.comprehensiveSearch(searchParams);
        if (paymentDetailsViews != null) {
            paymentDetailsViews.stream()
                    .peek(pd -> pd.setFullName(pd.getFirstName().concat(" ").concat(pd.getLastName()))).collect(Collectors.toList());
            Double billedAmountSum =
                    paymentDetailsViews.stream().filter(d -> d.getBilledAmount() != null).mapToDouble(PaymentDetailsView::getBilledAmount).sum();
            Double totalAmountSum =
                    paymentDetailsViews.stream().filter(d -> d.getTotalPaidAmount() != null).mapToDouble(PaymentDetailsView::getTotalPaidAmount).sum();
            PaymentDetailReturnObject paymentDetailReturnObject = new PaymentDetailReturnObject();
            paymentDetailReturnObject.setPaymentDetailsViewList(paymentDetailsViews);
            paymentDetailReturnObject.setBilledAmountSum(billedAmountSum);
            paymentDetailReturnObject.setTotalAmountSum(totalAmountSum);
            return paymentDetailReturnObject;
        }
        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", "No records found");
        return response;
    }

    @GetMapping("/user/referenceId/{userId}/{paymentSrc}")
    public String finReferenceIdByPaymentSrc( @PathVariable Long userId, @PathVariable String paymentSrc) {
        return paymentInfoService.getMaskedReferenceId(userId, paymentSrc);
    }

}
