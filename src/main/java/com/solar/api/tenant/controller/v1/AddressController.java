package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.user.address.AddressDTO;
import com.solar.api.tenant.mapper.user.address.AddressMapper;
import com.solar.api.tenant.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.user.address.AddressMapper.toAddressDTO;
import static com.solar.api.tenant.mapper.user.address.AddressMapper.toAddressDTOs;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AddressController")
@RequestMapping(value = "/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public AddressDTO add(@RequestBody AddressDTO addressDTO) {
        return AddressMapper.toAddressDTO(
                addressService.saveOrUpdate(AddressMapper.toAddress(addressDTO)));
    }

    @PutMapping
    public AddressDTO update(@RequestBody AddressDTO addressDTO) {
        return AddressMapper.toAddressDTO(
                addressService.saveOrUpdate(AddressMapper.toAddress(addressDTO)));
    }

//    @PreAuthorize("hasPermission(#id, 'Address')")
    @GetMapping("/{id}")
    public AddressDTO findById(@PathVariable Long id) {
        return toAddressDTO(addressService.findById(id));
    }

    @GetMapping("/getUser/{userId}")
    public List<AddressDTO> findByUserId(@PathVariable Long userId) {
        return toAddressDTOs(addressService.findAddressByUserAccount(userId));
    }

//    @PreAuthorize("hasPermission(#id, 'Address')")
    @GetMapping
    public List<AddressDTO> findAll() {
        return toAddressDTOs(addressService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        addressService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        addressService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

}
