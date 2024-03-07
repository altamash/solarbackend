package com.solar.api.tenant.service.trueup;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.model.extended.CsgBillcreRecon;
import com.solar.api.tenant.model.report.TrueUp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

public interface TrueUpService {

    TrueUp saveOrUpdate(MultipartFile multipartFile, Long subscription_id, Long subscriptionRateMatrixId,
                        String type) throws IOException, URISyntaxException, StorageException;

    List<TrueUp> getAllByGardenId(Long subscriptionRateMatrixId);

    void deleteByGarden(Long subscriptionRateMatrixId);

    TrueUp getBySubscriptionId(Long subscriptionId);

    void emailBySubscriptionRateMatrixId(String type, Long subscriptionRateMatrixId) throws IOException;

    TrueUp emailBySubscriptionId(String type, Long subscriptionId) throws IOException;

    List<CsgBillcreRecon> generate(String gardenId, List<Long> subscriptionIds, String startMonthYear,
                                   String endMonthYear) throws ParseException;

    List<CsgBillcreRecon> view(Long subscriptionId
            , String gardenId, String premiseNo, String periodStartDate, String periodEndDate);
}
