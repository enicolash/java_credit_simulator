package com.java.credit_simulator.controller;

import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.model.WebResponse;
import com.java.credit_simulator.service.CreditSimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class CreditSimulatorController {

    private final CreditSimulatorService creditSimulatorService;

    @PostMapping(
            path = "/calculate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CalculateResponse> calculate(@Valid @RequestBody CalculateRequest request){
        return WebResponse.<CalculateResponse>builder().data(creditSimulatorService.calculate(request)).build();
    }

}