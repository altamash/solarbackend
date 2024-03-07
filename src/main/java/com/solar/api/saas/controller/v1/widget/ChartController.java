package com.solar.api.saas.controller.v1.widget;

import com.solar.api.saas.mapper.widget.chart.ChartDetailDTO;
import com.solar.api.saas.mapper.widget.chart.ChartHeadDTO;
import com.solar.api.saas.mapper.widget.chart.ChartMapper;
import com.solar.api.saas.mapper.widget.chart.ChartResponse;
import com.solar.api.saas.service.widget.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

import static com.solar.api.saas.mapper.widget.chart.ChartMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ChartController")
@RequestMapping(value = "/widget")
public class ChartController {

    @Autowired
    private ChartService chartService;

    // ChartHead ////////////////////////////////////////////////
    @PostMapping
    public ChartHeadDTO add(@RequestBody ChartHeadDTO chartHeadDTO) {
        return ChartMapper.toChartHeadDTO(
                chartService.addOrUpdate(ChartMapper.toChartHead(chartHeadDTO)));
    }

    @PutMapping
    public ChartHeadDTO update(@RequestBody ChartHeadDTO addressDTO) {
        return ChartMapper.toChartHeadDTO(
                chartService.addOrUpdate(ChartMapper.toChartHead(addressDTO)));
    }

    @GetMapping("/{id}")
    public ChartHeadDTO findById(@PathVariable Long id) {
        return toChartHeadDTO(chartService.findById(id));
    }

    @GetMapping
    public List<ChartHeadDTO> findAll() {
        return toChartHeadDTOs(chartService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        chartService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        chartService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ChartDetail ////////////////////////////////////////////////
    @PostMapping("/detail")
    public ChartDetailDTO add(@RequestBody ChartDetailDTO chartDetailDTO) {
        return ChartMapper.toChartDetailDTO(
                chartService.addOrUpdateChartDetail(ChartMapper.toChartDetail(chartDetailDTO)));
    }

    @PutMapping("/detail")
    public ChartDetailDTO update(@RequestBody ChartDetailDTO addressDTO) {
        return ChartMapper.toChartDetailDTO(
                chartService.addOrUpdateChartDetail(ChartMapper.toChartDetail(addressDTO)));
    }

    @GetMapping("/detail/{id}")
    public ChartDetailDTO findChartDetailById(@PathVariable Long id) {
        return toChartDetailDTO(chartService.findChartDetailById(id));
    }

    @GetMapping("/detail")
    public List<ChartDetailDTO> findAllChartDetails() {
        return toChartDetailDTOs(chartService.findAllChartDetails());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteChartDetail(@PathVariable Long id) {
        chartService.deleteChartDetail(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllChartDetails() {
        chartService.deleteAllChartDetails();
        return new ResponseEntity(HttpStatus.OK);
    }

    // Charts API /////////////////////////////////////////////////
    @GetMapping("/chart")
    public ChartResponse getChartData(@RequestParam("widgetCode") String widgetCode,
                                      @RequestParam(name = "acct", required = false) Long accountId,
                                      @RequestParam(name = "subscription", required = false) Long subscriptionId) throws ParseException {
        return chartService.getChartData(widgetCode, accountId, subscriptionId);
    }
}
