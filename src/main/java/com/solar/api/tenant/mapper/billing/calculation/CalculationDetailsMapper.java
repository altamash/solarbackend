package com.solar.api.tenant.mapper.billing.calculation;

import com.solar.api.tenant.model.billing.calculation.CalculationDetails;

import java.util.List;
import java.util.stream.Collectors;

public class CalculationDetailsMapper {

    public static CalculationDetails toCalculationDetails(CalculationDetailsDTO calculationDetailsDTO) {
        if (calculationDetailsDTO == null) {
            return null;
        }
        return CalculationDetails.builder()
                .id(calculationDetailsDTO.getId())
                .calculationTracker(CalculationTrackerMapper.toCalculationTracker(calculationDetailsDTO.getCalculationTrackerDTO()))
                .attemptCount(calculationDetailsDTO.getAttemptCount())
                .state(calculationDetailsDTO.getState())
                .errorInd(calculationDetailsDTO.getErrorInd())
                .errorMessage(calculationDetailsDTO.getErrorMessage())
                .source(calculationDetailsDTO.getSource())
                .sourceId(calculationDetailsDTO.getSourceId())
                .invoiceId(calculationDetailsDTO.getInvoiceId())
                .reCalcInd(calculationDetailsDTO.getReCalcInd())
                .prevInvHtmlView(calculationDetailsDTO.getPrevInvHtmlView())
                .lockedInd(calculationDetailsDTO.getLockedInd())
                .publishState(calculationDetailsDTO.getPublishState())
                .build();
    }

    public static CalculationDetailsDTO toCalculationDetailsDTO(CalculationDetails calculationDetails) {
        if (calculationDetails == null) {
            return null;
        }
        return CalculationDetailsDTO.builder()
                .id(calculationDetails.getId())
                .calculationTrackerDTO(CalculationTrackerMapper.toCalculationTrackerDTO(calculationDetails.getCalculationTracker()))
                .attemptCount(calculationDetails.getAttemptCount())
                .state(calculationDetails.getState())
                .errorInd(calculationDetails.getErrorInd())
                .errorMessage(calculationDetails.getErrorMessage())
                .source(calculationDetails.getSource())
                .sourceId(calculationDetails.getSourceId())
                .invoiceId(calculationDetails.getInvoiceId())
                .reCalcInd(calculationDetails.getReCalcInd())
                .prevInvHtmlView(calculationDetails.getPrevInvHtmlView())
                .lockedInd(calculationDetails.getLockedInd())
                .publishState(calculationDetails.getPublishState())
                .build();    }

    public static CalculationDetails toUpdatedCalculationDetails(CalculationDetails calculationDetails, CalculationDetails calculationDetailsUpdate) {
        calculationDetails.setId(calculationDetailsUpdate.getId() == null ?
                calculationDetails.getId() :
                calculationDetailsUpdate.getId());
        calculationDetails.setCalculationTracker(calculationDetailsUpdate.getCalculationTracker() == null ?
                calculationDetails.getCalculationTracker() :
                calculationDetailsUpdate.getCalculationTracker());
        calculationDetails.setAttemptCount(calculationDetailsUpdate.getAttemptCount() == 0 ?
                calculationDetails.getAttemptCount() :
                calculationDetailsUpdate.getAttemptCount());
        calculationDetails.setErrorInd(calculationDetailsUpdate.getErrorInd() == null ?
                calculationDetails.getErrorInd() :
                calculationDetailsUpdate.getErrorInd());
        calculationDetails.setErrorMessage(calculationDetailsUpdate.getErrorMessage() == null ?
                calculationDetails.getErrorMessage() :
                calculationDetailsUpdate.getErrorMessage());
        calculationDetails.setState(calculationDetailsUpdate.getState() == null ?
                calculationDetails.getState() :
                calculationDetailsUpdate.getState());
        calculationDetails.setSource(calculationDetailsUpdate.getSource() == null ?
                calculationDetails.getSource() :
                calculationDetailsUpdate.getSource());
        calculationDetails.setInvoiceId(calculationDetailsUpdate.getInvoiceId() == null ?
                calculationDetails.getInvoiceId() :
                calculationDetailsUpdate.getInvoiceId());
        calculationDetails.setLockedInd(calculationDetailsUpdate.getLockedInd() == null ?
                calculationDetails.getLockedInd() :
                calculationDetailsUpdate.getLockedInd());
        calculationDetails.setPrevInvHtmlView(calculationDetailsUpdate.getPrevInvHtmlView() == null ?
                calculationDetails.getPrevInvHtmlView() :
                calculationDetailsUpdate.getPrevInvHtmlView());
        calculationDetails.setPublishState(calculationDetailsUpdate.getPublishState() == null ?
                calculationDetails.getPublishState() :
                calculationDetailsUpdate.getPublishState());
        calculationDetails.setReCalcInd(calculationDetailsUpdate.getReCalcInd() == null ?
                calculationDetails.getReCalcInd() :
                calculationDetailsUpdate.getReCalcInd());
        return calculationDetails;
    }

    public static List<CalculationDetails> toCalculationDetails(List<CalculationDetailsDTO> calculationDetailsDTOList) {
        if (calculationDetailsDTOList == null) {
            return null;
        }
        return calculationDetailsDTOList.stream().map(cr -> toCalculationDetails(cr)).collect(Collectors.toList());
    }

    public static List<CalculationDetailsDTO> toCalculationDetailsDTOs(List<CalculationDetails> calculationDetailsList) {
        if (calculationDetailsList == null) {
            return null;
        }
        return calculationDetailsList.stream().map(cr -> toCalculationDetailsDTO(cr)).collect(Collectors.toList());
    }
}
