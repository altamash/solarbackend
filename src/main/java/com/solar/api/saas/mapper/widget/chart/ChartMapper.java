package com.solar.api.saas.mapper.widget.chart;

import com.solar.api.saas.model.widget.chart.ChartDetail;
import com.solar.api.saas.model.widget.chart.ChartHead;

import java.util.List;
import java.util.stream.Collectors;

public class ChartMapper {

    // ChartHead /////////////////////////////////////////////////////////
    public static ChartHead toChartHead(ChartHeadDTO chartHeadDTO) {
        if (chartHeadDTO == null) {
            return null;
        }
        return ChartHead.builder()
                .chartId(chartHeadDTO.getChartId())
                .chartCode(chartHeadDTO.getChartCode())
                .chartName(chartHeadDTO.getChartName())
                .chartType(chartHeadDTO.getChartType())
                .maxXLabels(chartHeadDTO.getMaxXLabels())
                .maxXPoints(chartHeadDTO.getMaxXPoints())
                .minLabelWidth(chartHeadDTO.getMinLabelWidth())
                .showLegend(chartHeadDTO.getShowLegend())
                .enabled(chartHeadDTO.getEnabled())
                .orientation(chartHeadDTO.getOrientation())
                .javaMethod(chartHeadDTO.getJavaMethod())
                .build();
    }

    public static ChartHeadDTO toChartHeadDTO(ChartHead chartHead) {
        if (chartHead == null) {
            return null;
        }
        return ChartHeadDTO.builder()
                .chartId(chartHead.getChartId())
                .chartCode(chartHead.getChartCode())
                .chartName(chartHead.getChartName())
                .chartType(chartHead.getChartType())
                .maxXLabels(chartHead.getMaxXLabels())
                .maxXPoints(chartHead.getMaxXPoints())
                .minLabelWidth(chartHead.getMinLabelWidth())
                .showLegend(chartHead.getShowLegend())
                .enabled(chartHead.getEnabled())
                .orientation(chartHead.getOrientation())
                .javaMethod(chartHead.getJavaMethod())
                .createdAt(chartHead.getCreatedAt())
                .updatedAt(chartHead.getUpdatedAt())
                .build();
    }

    public static ChartHead toUpdatedChartHead(ChartHead chartHead, ChartHead chartHeadUpdate) {
        chartHead.setChartId(chartHeadUpdate.getChartId() == null ? chartHead.getChartId() :
                chartHeadUpdate.getChartId());
        chartHead.setChartCode(chartHeadUpdate.getChartCode() == null ? chartHead.getChartCode() :
                chartHeadUpdate.getChartCode());
        chartHead.setChartName(chartHeadUpdate.getChartName() == null ? chartHead.getChartName() :
                chartHeadUpdate.getChartName());
        chartHead.setChartType(chartHeadUpdate.getChartType() == null ? chartHead.getChartType() :
                chartHeadUpdate.getChartType());
        chartHead.setMaxXLabels(chartHeadUpdate.getMaxXLabels() == null ? chartHead.getMaxXLabels() :
                chartHeadUpdate.getMaxXLabels());
        chartHead.setMaxXPoints(chartHeadUpdate.getMaxXPoints() == null ? chartHead.getMaxXPoints() :
                chartHeadUpdate.getMaxXPoints());
        chartHead.setMinLabelWidth(chartHeadUpdate.getMinLabelWidth() == null ? chartHead.getMinLabelWidth() :
                chartHeadUpdate.getMinLabelWidth());
        chartHead.setShowLegend(chartHeadUpdate.getShowLegend() == null ? chartHead.getShowLegend() :
                chartHeadUpdate.getShowLegend());
        chartHead.setEnabled(chartHeadUpdate.getEnabled() == null ? chartHead.getEnabled() :
                chartHeadUpdate.getEnabled());
        chartHead.setOrientation(chartHeadUpdate.getOrientation() == null ? chartHead.getOrientation() :
                chartHeadUpdate.getOrientation());
        chartHead.setJavaMethod(chartHeadUpdate.getJavaMethod() == null ? chartHead.getJavaMethod() :
                chartHeadUpdate.getJavaMethod());

        return chartHead;
    }

    public static List<ChartHead> toChartHeades(List<ChartHeadDTO> chartHeadDTOs) {
        return chartHeadDTOs.stream().map(ch -> toChartHead(ch)).collect(Collectors.toList());
    }

    public static List<ChartHeadDTO> toChartHeadDTOs(List<ChartHead> chartHeads) {
        return chartHeads.stream().map(ch -> toChartHeadDTO(ch)).collect(Collectors.toList());
    }

    // ChartDetail /////////////////////////////////////////////////////////
    public static ChartDetail toChartDetail(ChartDetailDTO chartDetailDTO) {
        if (chartDetailDTO == null) {
            return null;
        }
        return ChartDetail.builder()
                .labelId(chartDetailDTO.getLabelId())
                .chartId(chartDetailDTO.getChartId())
                .seqNo(chartDetailDTO.getSeqNo())
                .labelName(chartDetailDTO.getLabelName())
                .borderColor(chartDetailDTO.getBorderColor())
                .baseColor(chartDetailDTO.getBaseColor())
                .build();
    }

    public static ChartDetailDTO toChartDetailDTO(ChartDetail chartDetail) {
        if (chartDetail == null) {
            return null;
        }
        return ChartDetailDTO.builder()
                .labelId(chartDetail.getLabelId())
                .chartId(chartDetail.getChartId())
                .seqNo(chartDetail.getSeqNo())
                .labelName(chartDetail.getLabelName())
                .borderColor(chartDetail.getBorderColor())
                .baseColor(chartDetail.getBaseColor())
                .createdAt(chartDetail.getCreatedAt())
                .updatedAt(chartDetail.getUpdatedAt())
                .build();
    }

    public static ChartDetail toUpdatedChartDetail(ChartDetail chartDetail, ChartDetail chartDetailUpdate) {
        chartDetail.setLabelId(chartDetailUpdate.getLabelId() == null ? chartDetail.getLabelId() :
                chartDetailUpdate.getLabelId());
        chartDetail.setChartId(chartDetailUpdate.getChartId() == null ? chartDetail.getChartId() :
                chartDetailUpdate.getChartId());
        chartDetail.setSeqNo(chartDetailUpdate.getSeqNo() == null ? chartDetail.getSeqNo() :
                chartDetailUpdate.getSeqNo());
        chartDetail.setLabelName(chartDetailUpdate.getLabelName() == null ? chartDetail.getLabelName() :
                chartDetailUpdate.getLabelName());
        chartDetail.setBorderColor(chartDetailUpdate.getBorderColor() == null ? chartDetail.getBorderColor() :
                chartDetailUpdate.getBorderColor());
        chartDetail.setBaseColor(chartDetailUpdate.getBaseColor() == null ? chartDetail.getBaseColor() :
                chartDetailUpdate.getBaseColor());
        return chartDetail;
    }

    public static List<ChartDetail> toChartDetailes(List<ChartDetailDTO> chartDetailDTOs) {
        return chartDetailDTOs.stream().map(cd -> toChartDetail(cd)).collect(Collectors.toList());
    }

    public static List<ChartDetailDTO> toChartDetailDTOs(List<ChartDetail> chartDetails) {
        return chartDetails.stream().map(cd -> toChartDetailDTO(cd)).collect(Collectors.toList());
    }
}
