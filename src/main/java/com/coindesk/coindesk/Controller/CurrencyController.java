package com.coindesk.coindesk.Controller;


import com.coindesk.coindesk.Service.CurrencySvc;
import com.coindesk.coindesk.model.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencySvc service;

    public CurrencyController(CurrencySvc service) {
        this.service = service;
    }

    @GetMapping
    public List<Currency> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        return ResponseEntity.ok(service.create(currency));
    }

    @PutMapping("/{code}")
    public ResponseEntity<Currency> update(@PathVariable String code, @RequestBody Currency currency) {
        return ResponseEntity.ok(service.update(code, currency));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
