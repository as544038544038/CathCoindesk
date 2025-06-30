package com.coindesk.coindesk.Service;


import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.coindesk.coindesk.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencySvc {

    private final CurrencyRepository repository;

    public CurrencySvc(CurrencyRepository repository) {
        this.repository = repository;
    }

    public List<Currency> findAll() {
        return repository.findAll()
                .stream()
                .sorted((c1, c2) -> c1.getCode().compareToIgnoreCase(c2.getCode()))
                .toList();
    }

    public Currency create(Currency currency) {
        return repository.save(currency);
    }

    public Currency update(String code, Currency currency) {
        Currency existing = repository.findById(code)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + code));
        existing.setNameZh(currency.getNameZh());
        return repository.save(existing);
    }

    public void delete(String code) {
        repository.deleteById(code);
    }
}
