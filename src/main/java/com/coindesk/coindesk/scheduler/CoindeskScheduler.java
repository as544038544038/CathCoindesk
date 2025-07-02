package com.coindesk.coindesk.scheduler;


import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.coindesk.coindesk.Service.CoindeskSvc;
import com.coindesk.coindesk.model.Currency;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CoindeskScheduler {

    private final CoindeskSvc coindeskSvc;
    private final CurrencyRepository currencyRepository;

    public CoindeskScheduler(CoindeskSvc coindeskService, CurrencyRepository currencyRepository) {
        this.coindeskSvc = coindeskService;
        this.currencyRepository = currencyRepository;
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void syncExchangeRate() {
        JsonNode data = coindeskSvc.getRawData();
        JsonNode bpi = data.path("bpi");

        bpi.fieldNames().forEachRemaining(code -> {
            JsonNode currencyNode = bpi.get(code);
            double rate = currencyNode.path("rate_float").asDouble();

            Optional<Currency> optionalCurrency = currencyRepository.findById(code);
            optionalCurrency.ifPresent(currency -> {
                currency.setRate(rate);
                currencyRepository.save(currency);
                log.info("更新匯率");
            });
        });
    }
}
