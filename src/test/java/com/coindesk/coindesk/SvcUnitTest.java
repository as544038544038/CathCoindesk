package com.coindesk.coindesk;

import com.coindesk.coindesk.DTO.ConvertedCurrencyDTO;
import com.coindesk.coindesk.DTO.ConvertedResponseDTO;
import com.coindesk.coindesk.model.Currency;
import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.coindesk.coindesk.Service.CoindeskSvc;
import com.coindesk.coindesk.Service.CurrencySvc;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SvcUnitTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencySvc currencySvc;

    private Currency sample;

    @Mock
    private ObjectMapper objectMapper;

    private CoindeskSvc coindeskSvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sample = new Currency("USD", "美元", 23342.01);
        coindeskSvc = new CoindeskSvc(currencyRepository);
    }

    @Test
    void testFindAllSortedByCode() {
        when(currencyRepository.findAllByOrderByCodeAsc()).thenReturn(List.of(sample));
        List<Currency> result = currencySvc.findAllSortedByCode();
        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getCode());
    }

    @Test
    void testCreateCurrency() {
        when(currencyRepository.save(sample)).thenReturn(sample);
        Currency saved = currencySvc.create(sample);
        assertEquals("USD", saved.getCode());
    }

    @Test
    void testUpdateCurrencyFound() {
        Currency newData = new Currency("USD", "美金", 25000.0);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(sample));
        when(currencyRepository.save(any(Currency.class))).thenReturn(newData);
        Currency updated = currencySvc.update("USD", newData);
        assertEquals("美金", updated.getNameZh());
    }

    @Test
    void testUpdateCurrencyNotFound() {
        when(currencyRepository.findById("USD")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                currencySvc.update("USD", sample));
        assertTrue(ex.getMessage().contains("Currency not found"));
    }

    @Test
    void testDeleteCurrency() {
        doNothing().when(currencyRepository).deleteById("USD");
        currencySvc.delete("USD");
        verify(currencyRepository, times(1)).deleteById("USD");
    }

    @Test
    void testGetConvertedDataMocked() {
        // This test mocks getRawData and runs transformation logic
        CoindeskSvc svc = spy(new CoindeskSvc(currencyRepository));
        String json = "{\"time\":{\"updatedISO\":\"2022-08-03T20:25:00+00:00\"},\"bpi\":{\"USD\":{\"code\":\"USD\",\"rate_float\":23342.01}}}";

        try {
            JsonNode node = new ObjectMapper().readTree(json);
            doReturn(node).when(svc).getRawData();
            when(currencyRepository.findById("USD")).thenReturn(Optional.of(sample));

            ConvertedResponseDTO response = svc.getConvertedData();
            assertEquals("2022/08/03 20:25:00", response.getUpdateTime());
            assertEquals("USD", response.getCurrencyList().get(0).getCode());
            assertEquals("美元", response.getCurrencyList().get(0).getNameZh());
        } catch (Exception e) {
            fail("Exception in CoindeskSvc test: " + e.getMessage());
        }
    }
}

