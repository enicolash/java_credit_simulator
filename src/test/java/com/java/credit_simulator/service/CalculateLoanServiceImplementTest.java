package com.java.credit_simulator.service;

import com.google.gson.Gson;
import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.service.impl.CalculateLoanServiceImplement;
import com.java.credit_simulator.util.IsExisting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CalculateLoanServiceImplementTest {
    private CalculateLoanServiceImplement calculateNewLoanService;

    private Gson gson = new Gson();


    @BeforeEach
    void setUp() {
        calculateNewLoanService = CalculateLoanServiceImplement.builder().build();
    }

    private String getCalculateUsedCarResponse = "{\n" +
            "    \"yearlyInformations\": [\n" +
            "        {\n" +
            "            \"year\": 1,\n" +
            "            \"interestRate\": 8.0,\n" +
            "            \"principalAmount\": \"75000000.00\",\n" +
            "            \"totalLoanAmount\": \"81000000.00\",\n" +
            "            \"installmentMonthly\": \"2250000.00\",\n" +
            "            \"installmentYearly\": \"27000000.00\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"year\": 2,\n" +
            "            \"interestRate\": 8.1,\n" +
            "            \"principalAmount\": \"54000000.00\",\n" +
            "            \"totalLoanAmount\": \"58374000.00\",\n" +
            "            \"installmentMonthly\": \"2432000.00\",\n" +
            "            \"installmentYearly\": \"29184000.00\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"year\": 3,\n" +
            "            \"interestRate\": 8.6,\n" +
            "            \"principalAmount\": \"29187000.00\",\n" +
            "            \"totalLoanAmount\": \"31697082.00\",\n" +
            "            \"installmentMonthly\": \"2641423.50\",\n" +
            "            \"installmentYearly\": \"31697082.00\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"installmentMonthlyAverage\": \"2441224.50\"\n" +
            "}";

    private String getResponseThirdParty = "{\n" +
            "    \"vehicleType\": \"Mobil\",\n" +
            "    \"vehicleCondition\": \"Baru\",\n" +
            "    \"vehicleYear\": 2025,\n" +
            "    \"totalLoanAmount\": 1000000000,\n" +
            "    \"loanTenure\": 6,\n" +
            "    \"downPayment\": 500000000\n" +
            "}";
    @Test
    void calculate_UsedCarLoan_Success() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Baru")
                .vehicleYear(2024)
                .totalLoanAmount(100000000.0)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.N)
                .build();

        CalculateResponse actual = calculateNewLoanService.calculate(request);

        CalculateResponse expected = gson.fromJson(getCalculateUsedCarResponse,CalculateResponse.class);


        assertNotNull(actual);
        assertNotNull(actual.getYearlyInformations());
        assertEquals(expected.getYearlyInformations().size(), actual.getYearlyInformations().size());
        assertEquals(expected.getInstallmentMonthlyAverage(), actual.getInstallmentMonthlyAverage());

        assertEquals(expected.getYearlyInformations().get(0).getYear(),
                actual.getYearlyInformations().get(0).getYear());
        assertEquals(expected.getYearlyInformations().get(0).getInterestRate(),
                actual.getYearlyInformations().get(0).getInterestRate());
        assertEquals(expected.getYearlyInformations().get(0).getPrincipalAmount(),
                actual.getYearlyInformations().get(0).getPrincipalAmount());

        assertEquals(expected.getYearlyInformations().get(1).getYear(),
                actual.getYearlyInformations().get(1).getYear());
        assertEquals(expected.getYearlyInformations().get(1).getInterestRate(),
                actual.getYearlyInformations().get(1).getInterestRate());
        assertEquals(expected.getYearlyInformations().get(1).getPrincipalAmount(),
                actual.getYearlyInformations().get(1).getPrincipalAmount());

        assertEquals(expected.getYearlyInformations().get(2).getYear(),
                actual.getYearlyInformations().get(2).getYear());
        assertEquals(expected.getYearlyInformations().get(2).getInterestRate(),
                actual.getYearlyInformations().get(2).getInterestRate());
        assertEquals(expected.getYearlyInformations().get(2).getPrincipalAmount(),
                actual.getYearlyInformations().get(2).getPrincipalAmount());

    }
}
