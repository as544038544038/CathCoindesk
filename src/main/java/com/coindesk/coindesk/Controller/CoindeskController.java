package com.coindesk.coindesk.Controller;


import com.coindesk.coindesk.DTO.ConvertedResponseDTO;
import com.coindesk.coindesk.Service.CoindeskSvc;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coindesk")
public class CoindeskController {

    private final CoindeskSvc coindeskSvc;

    public CoindeskController(CoindeskSvc coindeskSvc) {
        this.coindeskSvc = coindeskSvc;
    }

    @GetMapping("/raw")
    public JsonNode getRaw() {
        return coindeskSvc.getRawData();
    }

    @GetMapping("/converted")
    public ConvertedResponseDTO getConverted() {
        return coindeskSvc.getConvertedData();
    }
}
