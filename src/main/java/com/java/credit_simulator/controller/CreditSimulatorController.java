package com.java.credit_simulator.controller;

import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.model.WebResponse;
import com.java.credit_simulator.service.CreditSimulatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Credit Simulator", description = "API for vehicle loan simulation")
public class CreditSimulatorController {

    private final CreditSimulatorService creditSimulatorService;

    @Operation(
            summary = "Calculate vehicle loan simulation",
            description = "Calculate monthly installments based on vehicle loan amount and tenure requested"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated loan simulation",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(
            path = "/calculate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CalculateResponse> calculate(@Valid @RequestBody CalculateRequest request){
        return WebResponse.<CalculateResponse>builder().data(creditSimulatorService.calculate(request)).build();
    }

}