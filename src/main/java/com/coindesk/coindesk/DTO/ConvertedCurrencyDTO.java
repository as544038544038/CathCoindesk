package com.coindesk.coindesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertedCurrencyDTO {
    private String code;
    private String nameZh;
    private Double rate;
}
