package com.coindesk.coindesk.DAO;


import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.coindesk.coindesk.model.Currency;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrencyDAO {

    private final CurrencyRepository repository;

    public CurrencyDAO(CurrencyRepository repository) {
        this.repository = repository;
    }

    public void initCurrencyDataIfEmpty() {
        if (repository.count() == 0) {
            List<Currency> initData = List.of(
                    new Currency("USD", "美元",0.0),
                    new Currency("EUR", "歐元",0.0),
                    new Currency("GBP", "英鎊",0.0)
            );
            repository.saveAll(initData);
        }
    }
}
