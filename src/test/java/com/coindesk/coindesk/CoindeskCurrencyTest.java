package com.coindesk.coindesk;

import com.coindesk.coindesk.Controller.CoindeskController;
import com.coindesk.coindesk.Controller.CurrencyController;
import com.coindesk.coindesk.model.Currency;
import com.coindesk.coindesk.Service.CoindeskSvc;
import com.coindesk.coindesk.Service.CurrencySvc;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CoindeskController.class, CurrencyController.class})
public class CoindeskCurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoindeskSvc coindeskSvc;

    @MockBean
    private CurrencySvc currencySvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Currency sample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sample = new Currency("USD", "美元", 23342.01);
    }

    @Test
    void testGetAllCurrencies() throws Exception {
        when(currencySvc.findAllSortedByCode()).thenReturn(List.of(sample));
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USD"));
    }

    @Test
    void testCreateCurrency() throws Exception {
        when(currencySvc.create(any(Currency.class))).thenReturn(sample);
        mockMvc.perform(post("/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"));
    }

    @Test
    void testUpdateCurrency() throws Exception {
        Currency updated = new Currency("USD", "美金", 24000.0);
        when(currencySvc.update(eq("USD"), any(Currency.class))).thenReturn(updated);

        mockMvc.perform(put("/currencies/USD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameZh").value("美金"));
    }

    @Test
    void testDeleteCurrency() throws Exception {
        doNothing().when(currencySvc).delete("USD");
        mockMvc.perform(delete("/currencies/USD"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetCoindeskRaw() throws Exception {
        String json = "{\"bpi\":{\"USD\":{\"rate_float\":23342.01}},\"time\":{\"updatedISO\":\"2022-08-03T20:25:00+00:00\"}}";
        JsonNode node = new ObjectMapper().readTree(json);
        when(coindeskSvc.getRawData()).thenReturn(node);

        mockMvc.perform(get("/coindesk/raw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bpi.USD.rate_float").value(23342.01));
    }

    @Test
    void testGetCoindeskConverted() throws Exception {
        when(coindeskSvc.getConvertedData()).thenReturn(
                new com.coindesk.coindesk.DTO.ConvertedResponseDTO(
                        "2022/08/03 20:25:00",
                        List.of(new com.coindesk.coindesk.DTO.ConvertedCurrencyDTO("USD", "美元", 23342.01))
                )
        );

        mockMvc.perform(get("/coindesk/converted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyList[0].code").value("USD"));
    }
}
