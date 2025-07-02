package com.coindesk.coindesk.demo;

import com.coindesk.coindesk.DAO.CurrencyDAO;
import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.coindesk.coindesk.model.Currency;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initCurrency(CurrencyDAO dao) {
        return args -> dao.initCurrencyDataIfEmpty();
    }
}
