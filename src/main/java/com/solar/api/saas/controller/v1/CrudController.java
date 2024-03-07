package com.solar.api.saas.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface CrudController<U> {

    @GetMapping( "/{id}")
    ResponseEntity<?> getById(@PathVariable("id") Long id);

    @GetMapping()
    ResponseEntity<?> getAll();

    @PostMapping()
    ResponseEntity<?> save(@RequestBody U obj);

    @PutMapping()
    ResponseEntity<?> update(@RequestBody U obj);
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
