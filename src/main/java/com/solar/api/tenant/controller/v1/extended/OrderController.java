package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.order.ClaimFileDTO;
import com.solar.api.tenant.mapper.extended.order.OrderDetailDTO;
import com.solar.api.tenant.mapper.extended.order.OrderHeadDTO;
import com.solar.api.tenant.model.extended.order.OrderDetail;
import com.solar.api.tenant.model.extended.order.OrderHead;
import com.solar.api.tenant.repository.order.OrderDetailRepository;
import com.solar.api.tenant.repository.order.OrderHeadRepository;
import com.solar.api.tenant.service.extended.order.ClaimFileService;
import com.solar.api.tenant.service.extended.order.OrderDetailService;
import com.solar.api.tenant.service.extended.order.OrderHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.order.OrderMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("OrderController")
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    private OrderHeadService orderHeadService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ClaimFileService claimFileService;
    @Autowired
    private OrderHeadRepository orderHeadRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // OrderHead ////////////////////////////////////////
    @PostMapping("/head")
    public OrderHeadDTO addOrderHead(@RequestBody OrderHeadDTO orderHeadDTO) {
        return toOrderHeadDTO(orderHeadService.save(toOrderHead(orderHeadDTO)));
    }

    @PutMapping("/head")
    public OrderHeadDTO updateOrderHead(@RequestBody OrderHeadDTO orderHeadDTO) {
        OrderHead orderHead = orderHeadRepository.findById(orderHeadDTO.getId()).orElse(null);
        return toOrderHeadDTO(orderHead == null ? orderHead : orderHeadService.save(toUpdatedOrderHead(orderHead,
                toOrderHead(orderHeadDTO))));
    }

    @GetMapping("/head/{id}")
    public OrderHeadDTO findOrderHeadById(@PathVariable Long id) {
        return toOrderHeadDTO(orderHeadService.findById(id));
    }

    @GetMapping("/head")
    public List<OrderHeadDTO> findAllOrderHeads() {
        return toOrderHeadDTOs(orderHeadService.findAll());
    }

    @DeleteMapping("/head/{id}")
    public ResponseEntity deleteOrderHead(@PathVariable Long id) {
        orderHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllOrderHeads() {
        orderHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // OrderDetail ////////////////////////////////////////
    @PostMapping("/detail")
    public OrderDetailDTO addOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        return toOrderDetailDTO(orderDetailService.save(toOrderDetail(orderDetailDTO)));
    }

    @PutMapping("/detail")
    public OrderDetailDTO updateOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailDTO.getId()).orElse(null);
        return toOrderDetailDTO(orderDetail == null ? orderDetail :
                orderDetailService.save(toUpdatedOrderDetail(orderDetail, toOrderDetail(orderDetailDTO))));
    }

    @GetMapping("/detail/{id}")
    public OrderDetailDTO findOrderDetailById(@PathVariable Long id) {
        return toOrderDetailDTO(orderDetailService.findById(id));
    }

    @GetMapping("/detail")
    public List<OrderDetailDTO> findAllDocuLibraries() {
        return toOrderDetailDTOs(orderDetailService.findAll());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteOrderDetail(@PathVariable Long id) {
        orderDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllDocuLibraries() {
        orderDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ClaimFile ////////////////////////////////////////
    @PostMapping("/claimFile")
    public ClaimFileDTO addClaimFile(@RequestBody ClaimFileDTO claimFileDTO) {
        return toClaimFileDTO(claimFileService.save(toClaimFile(claimFileDTO)));
    }

    @PutMapping("/claimFile")
    public ClaimFileDTO updateClaimFile(@RequestBody ClaimFileDTO claimFileDTO) {
        return toClaimFileDTO(claimFileService.save(toClaimFile(claimFileDTO)));
    }

    @GetMapping("/claimFile/{id}")
    public ClaimFileDTO findClaimFileById(@PathVariable Long id) {
        return toClaimFileDTO(claimFileService.findById(id));
    }

    @GetMapping("/claimFile")
    public List<ClaimFileDTO> findAllClaimFiles() {
        return toClaimFileDTOs(claimFileService.findAll());
    }

    @DeleteMapping("/claimFile/{id}")
    public ResponseEntity deleteClaimFile(@PathVariable Long id) {
        claimFileService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/claimFile")
    public ResponseEntity deleteAllClaimFiles() {
        claimFileService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
