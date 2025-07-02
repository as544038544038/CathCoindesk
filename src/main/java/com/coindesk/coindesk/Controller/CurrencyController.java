package com.coindesk.coindesk.Controller;


import com.coindesk.coindesk.Service.CurrencySvc;
import com.coindesk.coindesk.model.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencySvc currencySvc;

    public CurrencyController(CurrencySvc currencySvc) {
        this.currencySvc = currencySvc;
    }

    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencySvc.findAllSortedByCode();
    }

    @PostMapping
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        return ResponseEntity.ok(currencySvc.create(currency));
    }

    @PutMapping("/{code}")
    public ResponseEntity<Currency> update(@PathVariable String code, @RequestBody Currency currency) {
        return ResponseEntity.ok(currencySvc.update(code, currency));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        currencySvc.delete(code);
        return ResponseEntity.noContent().build();
    }
}
