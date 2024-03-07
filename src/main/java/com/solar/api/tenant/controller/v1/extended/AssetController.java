package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.extended.assetHead.*;
import com.solar.api.tenant.service.extended.assetHead.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.assetHead.AssetBlockDetailMapper.toAssetBlockDetailDTOs;
import static com.solar.api.tenant.mapper.extended.assetHead.AssetBlockDetailMapper.toAssetBlockDetails;
import static com.solar.api.tenant.mapper.extended.assetHead.AssetMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AssetController")
@RequestMapping(value = "/asset")
public class AssetController {

    @Autowired
    private AssetHeadService assetHeadService;
    @Autowired
    private AssetDetailService assetDetailService;
    @Autowired
    private AssetSupplierService assetSupplierService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private AssetSerialNumberService assetSerialNumberService;
    @Autowired
    private ScanCodesService scanCodesService;
    @Autowired
    private AssetBlockDetailService assetBlockDetailService;

    // AssetHead ////////////////////////////////////////
    @PostMapping("/head")
    public AssetHeadDTO addAssetHead(@RequestBody AssetHeadDTO assetHeadDTO) {
        return toAssetHeadDTO(assetHeadService.save(toAssetHead(assetHeadDTO)));
    }

    @PutMapping("/head")
    public AssetHeadDTO updateAssetHeadAndDetails(@RequestBody AssetHeadDTO assetHeadDTO) {
        AssetHeadDTO assetHeadDTO1 = toAssetHeadDTO(assetHeadService.update(toAssetHead(assetHeadDTO)));
        assetHeadDTO1.setAssetDetails(toAssetDetailDTOs(assetDetailService.update(toAssetDetails(assetHeadDTO.getAssetDetails()),toAssetHead(assetHeadDTO1))));
        return assetHeadDTO1;
    }

    @GetMapping("/head/{id}")
    public AssetHeadDTO findAssetHeadById(@PathVariable Long id) {
        return toAssetHeadDTO(assetHeadService.findById(id));
    }

    @GetMapping("/head")
    public List<AssetHeadDTO> findAllAssetHeads() {
        return toAssetHeadDTOs(assetHeadService.findAll());
    }

    @GetMapping("/headAndDetails/{registerHeadId}")
    public List<AssetHeadDTO> findAssetHeadAndDetails(@PathVariable Long registerHeadId) {
        return toAssetHeadDTOs(assetHeadService.findAllByRegisterId(registerHeadId));
    }

    @DeleteMapping("/head/{id}")
    public ResponseEntity deleteAssetHead(@PathVariable Long id) {
        assetHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllAssetHeads() {
        assetHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // AssetDetail ////////////////////////////////////////
    @PostMapping("/detail")
    public AssetDetailDTO addAssetDetail(@RequestBody AssetDetailDTO assetDetailDTO) {
        return toAssetDetailDTO(assetDetailService.save(toAssetDetail(assetDetailDTO)));
    }

    @PutMapping("/detail")
    public AssetDetailDTO updateAssetDetail(@RequestBody AssetDetailDTO assetDetailDTO) {
        return toAssetDetailDTO(assetDetailService.save(toAssetDetail(assetDetailDTO)));
    }

    @GetMapping("/detail/{id}")
    public AssetDetailDTO findAssetDetailById(@PathVariable Long id) {
        return toAssetDetailDTO(assetDetailService.findById(id));
    }

    @GetMapping("/detail")
    public List<AssetDetailDTO> findAllAssetDetails() {
        return toAssetDetailDTOs(assetDetailService.findAll());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteAssetDetail(@PathVariable Long id) {
        assetDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllAssetDetails() {
        assetDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    /*@PostMapping("/headAndDetails")
    public AssetHeadDTO addAssetHeadAndDetails(@RequestBody AssetHeadDTO assetHeadDTO) {
        return toAssetHeadDTO(assetHeadService.save(toAssetHead(assetHeadDTO)));
    }*/

    // AssetSupplier ////////////////////////////////////////
    @PostMapping("/supplier")
    public AssetSupplierDTO addAssetSupplier(@RequestBody AssetSupplierDTO assetSupplierDTO) {
        return toAssetSupplierDTO(assetSupplierService.save(toAssetSupplier(assetSupplierDTO)));
    }

    @PutMapping("/supplier")
    public AssetSupplierDTO updateAssetSupplier(@RequestBody AssetSupplierDTO assetSupplierDTO) {
        return toAssetSupplierDTO(assetSupplierService.save(toAssetSupplier(assetSupplierDTO)));
    }

    @GetMapping("/supplier/{id}")
    public AssetSupplierDTO findAssetSupplierById(@PathVariable Long id) {
        return toAssetSupplierDTO(assetSupplierService.findById(id));
    }

    @GetMapping("/supplier")
    public List<AssetSupplierDTO> findAllAssetSuppliers() {
        return toAssetSupplierDTOs(assetSupplierService.findAll());
    }

    @DeleteMapping("/supplier/{id}")
    public ResponseEntity deleteAssetSupplier(@PathVariable Long id) {
        assetSupplierService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/supplier")
    public ResponseEntity deleteAllAssetSuppliers() {
        assetSupplierService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // Inventory ////////////////////////////////////////
    @PostMapping("/inventory")
    public InventoryDTO addInventory(@RequestBody InventoryDTO inventoryDTO) {
        return toInventoryDTO(inventoryService.save(toInventory(inventoryDTO)));
    }

    @PutMapping("/inventory")
    public InventoryDTO updateInventory(@RequestBody InventoryDTO inventoryDTO) {
        return toInventoryDTO(inventoryService.save(toInventory(inventoryDTO)));
    }

    @GetMapping("/inventory/{id}")
    public InventoryDTO findInventoryById(@PathVariable Long id) {
        return toInventoryDTO(inventoryService.findById(id));
    }

    @GetMapping("/inventory")
    public List<InventoryDTO> findAllInventories() {
        return toInventoryDTOs(inventoryService.findAll());
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity deleteInventory(@PathVariable Long id) {
        inventoryService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/inventory")
    public ResponseEntity deleteAllInventories() {
        inventoryService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // AssetSerialNumber ////////////////////////////////////////
    @PostMapping("/assetSerialNumber")
    public AssetSerialNumberDTO addAssetSerialNumbers(@RequestBody AssetSerialNumberDTO assetSerialNumberDTO) {
        return toAssetSerialNumbersDTO(assetSerialNumberService.save(toAssetSerialNumbers(assetSerialNumberDTO)));
    }

    @PutMapping("/assetSerialNumber")
    public AssetSerialNumberDTO updateAssetSerialNumbers(@RequestBody AssetSerialNumberDTO assetSerialNumberDTO) {
        return toAssetSerialNumbersDTO(assetSerialNumberService.save(toAssetSerialNumbers(assetSerialNumberDTO)));
    }

    @GetMapping("/assetSerialNumber/{id}")
    public AssetSerialNumberDTO findAssetSerialNumbersById(@PathVariable Long id) {
        return toAssetSerialNumbersDTO(assetSerialNumberService.findById(id));
    }

    @GetMapping("/assetSerialNumber")
    public List<AssetSerialNumberDTO> findAllAssetSerialNumbers() {
        return toAssetSerialNumbersDTOs(assetSerialNumberService.findAll());
    }

    @GetMapping("/assetSerialNumber/findByAssetId/{assetId}")
    public List<AssetSerialNumberDTO> findAssetSerialNumbersByAssetId(@PathVariable Long assetId) {
        return toAssetSerialNumbersDTOs(assetSerialNumberService.findAllByAssetId(assetId));
    }

    @GetMapping("/assetSerialNumber/findFilteredSerialByAssetId/{assetId}")
    public List<AssetSerialNumberDTO> findFilteredSerialByAssetId(@PathVariable Long assetId) {
        return toAssetSerialNumbersDTOs(assetSerialNumberService.findFilteredSerialByAssetId(assetId));
    }

    @GetMapping("/assetSerialNumber/paged/findFilteredSerialByAssetId/{assetId}/{page}/{pageSize}/{sort}")
    public PagedAssetSerialNumberDTO findPagedFilteredSerialByAssetId(@PathVariable Long assetId,
                                                                       @PathVariable("page") int pageNumber,
                                                                       @PathVariable("pageSize") Integer pageSize,
                                                                       @PathVariable("sort") String sort) {
        return assetSerialNumberService.findPagedFilteredSerialByAssetId(assetId, pageNumber, pageSize, sort);
    }

    @GetMapping("/assetSerialNumber/paged/searchSerialNoByPalletNoAndAssetId/{assetId}/{palletNo}/{page}/{pageSize}/{sort}")
    public PagedAssetSerialNumberDTO searchSerialNoByPalletNoAndAssetId(@PathVariable Long assetId,
                                                                        @PathVariable("palletNo") String palletNo,
                                                                        @PathVariable("page") int pageNumber,
                                                                        @PathVariable("pageSize") Integer pageSize,
                                                                        @PathVariable("sort") String sort) {
        return assetSerialNumberService.searchSerialNoByPalletNoAndAssetId(assetId,palletNo, pageNumber, pageSize, sort);
    }

    @GetMapping("/assetSerialNumber/palletNumbers/{assetId}")
    public List<String> distinctPalletNumbersByAssetId(@PathVariable Long assetId) {
        return assetSerialNumberService.distinctPalletNumbersByAssetId(assetId);
    }

    @DeleteMapping("/assetSerialNumber/{id}")
    public ResponseEntity deleteAssetSerialNumbers(@PathVariable Long id) {
        assetSerialNumberService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/assetSerialNumber")
    public ResponseEntity deleteAllAssetSerialNumbers() {
        assetSerialNumberService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ScanCodes ///////////////////////////////////////
    @PostMapping("/scanCodes")
    public ScanCodesDTO addScanCodes(@RequestBody ScanCodesDTO scanCodesDTO) {
        return toScanCodesDTO(scanCodesService.save(toScanCodes(scanCodesDTO)));
    }

    @PutMapping("/scanCodes")
    public ScanCodesDTO updateScanCodes(@RequestBody ScanCodesDTO scanCodesDTO) {
        return toScanCodesDTO(scanCodesService.save(toScanCodes(scanCodesDTO)));
    }

    @GetMapping("/scanCodes/{id}")
    public ScanCodesDTO findScanCodesById(@PathVariable Long id) {
        return toScanCodesDTO(scanCodesService.findById(id));
    }

    @GetMapping("/scanCodes")
    public List<ScanCodesDTO> findAllScanCodes() {
        return toScanCodesDTOs(scanCodesService.findAll());
    }

    @DeleteMapping("/scanCodes/{id}")
    public ResponseEntity deleteScanCodes(@PathVariable Long id) {
        scanCodesService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/scanCodes")
    public ResponseEntity deleteAllScanCodes() {
        scanCodesService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // AssetBlockDetail ////////////////////////////////////////
    @PostMapping("/blockDetail")
    public List<AssetBlockDetailDTO> addAssetBlockDetails(@RequestBody List<AssetBlockDetailDTO> assetBlockDetailDTOs) {
        return toAssetBlockDetailDTOs(assetBlockDetailService.saveAll(toAssetBlockDetails(assetBlockDetailDTOs)));
    }

    @GetMapping("/blockDetail/{assetId}")
    public List<AssetBlockDetailDTO> findAllByAssetId(@PathVariable Long assetId) {
        return toAssetBlockDetailDTOs(assetBlockDetailService.findAllByAssetId(assetId));
    }

    @GetMapping("/blockDetail/getAllBlockValues/{registerId}/{assetId}/{blockId}/{pageNumber}/{pageSize}/{sort}")
    public PagedAssetBlockDetailDTO getAllBlockValuesByAssetId (@PathVariable Long registerId, @PathVariable Long assetId, @PathVariable Long blockId,
                                                         @PathVariable int pageNumber, @PathVariable Integer pageSize, @PathVariable String sort) {
        return assetBlockDetailService.getAllBlockValuesByAssetId(registerId,assetId,blockId,pageNumber,pageSize,sort);
    }

    @PutMapping("/blockDetail/head")
    public List<AssetBlockDetailDTO> updateAssetBlockDetails(@RequestBody List<AssetBlockDetailDTO> assetBlockDetailDTOs) {
        return toAssetBlockDetailDTOs(assetBlockDetailService.update(toAssetBlockDetails(assetBlockDetailDTOs)));
    }

    @DeleteMapping("/blockDetail/{assetRefId}")
    public ObjectNode deleteAssetBlock(@PathVariable Long assetRefId) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("msg", assetBlockDetailService.deleteAssetBlock(assetRefId));
        return messageJson;
    }
}
