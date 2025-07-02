package com.coindesk.coindesk.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConvertedResponseDTO {
    private String updateTime;
    private List<ConvertedCurrencyDTO> currencyList;
}
