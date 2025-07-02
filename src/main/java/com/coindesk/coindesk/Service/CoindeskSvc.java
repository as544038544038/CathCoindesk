package com.coindesk.coindesk.Service;

import com.coindesk.coindesk.DTO.ConvertedCurrencyDTO;
import com.coindesk.coindesk.DTO.ConvertedResponseDTO;
import com.coindesk.coindesk.Repository.CurrencyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class CoindeskSvc {

    private final CurrencyRepository currencyRepository;

    public CoindeskSvc(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Value("${coindesk.api.url}")
    private String coindeskApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getRawData() {
        try {
            String json = restTemplate.getForObject(coindeskApiUrl, String.class);
            ObjectNode root = (ObjectNode) objectMapper.readTree(json);

            ObjectNode bpiNode = (ObjectNode) root.get("bpi");
            List<String> codes = new ArrayList<>();
            bpiNode.fieldNames().forEachRemaining(codes::add);

            codes.sort(String::compareTo);

            ObjectNode sortedBpi = objectMapper.createObjectNode();
            for (String code : codes) {
                sortedBpi.set(code, bpiNode.get(code));
            }

            root.set("bpi", sortedBpi);

            return root;
        } catch (Exception e) {
            log.debug("API Error and get mock");
            return getMockDataSorted();
        }
    }

    private JsonNode getMockDataSorted() {
        try (InputStream is = new ClassPathResource("mock/coindesk-mock.json").getInputStream()) {
            ObjectNode root = (ObjectNode) objectMapper.readTree(is);
            ObjectNode bpiNode = (ObjectNode) root.get("bpi");
            List<String> codes = new ArrayList<>();
            bpiNode.fieldNames().forEachRemaining(codes::add);
            codes.sort(String::compareTo);

            ObjectNode sortedBpi = objectMapper.createObjectNode();
            for (String code : codes) {
                sortedBpi.set(code, bpiNode.get(code));
            }
            root.set("bpi", sortedBpi);
            return root;
        } catch (Exception e) {
            throw new RuntimeException("mock data error", e);
        }
    }

    public ConvertedResponseDTO getConvertedData() {
        JsonNode raw = getRawData();

        String updatedISO = raw.path("time").path("updatedISO").asText();
        String formattedTime = updatedISO.replace("T", " ").substring(0, 19).replace("-", "/");

        List<ConvertedCurrencyDTO> currencyList = new ArrayList<>();

        JsonNode bpi = raw.path("bpi");

        bpi.fieldNames().forEachRemaining(code -> {
            JsonNode currencyNode = bpi.get(code);
            String currencyCode = currencyNode.path("code").asText();
            double rate = currencyNode.path("rate_float").asDouble();
            String nameZh = currencyRepository.findById(currencyCode)
                    .map(c -> c.getNameZh())
                    .orElse("Unkown");

            currencyList.add(new ConvertedCurrencyDTO(currencyCode, nameZh, rate));
        });

        currencyList.sort(Comparator.comparing(ConvertedCurrencyDTO::getCode));

        return new ConvertedResponseDTO(formattedTime, currencyList);
    }
}
