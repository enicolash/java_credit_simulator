package com.java.credit_simulator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing vehicle loan calculation results")
public class CalculateResponse {
    @Schema(description = "List of yearly loan information breakdown")
    private List<YearlyInformation> yearlyInformations;

    @Schema(description = "Average monthly installment amount", example = "15,000,000")
    private BigDecimal installmentMonthlyAverage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Yearly loan information details")
    public static class YearlyInformation {

        @Schema(description = "Loan year", example = "1")
        private Integer year;

        @Schema(description = "Interest rate for the year", example = "0.07")
        private Double interestRate;

        @Schema(description = "Principal loan amount", example = "300,000,000")
        private BigDecimal principalAmount;

        @Schema(description = "Total loan amount including interest", example = "500,000,000")
        private BigDecimal totalLoanAmount;

        @Schema(description = "Monthly installment amount", example = "15,000,000")
        private BigDecimal installmentMonthly;

        @Schema(description = "Yearly installment amount", example = "180,000,000")
        private BigDecimal installmentYearly;
    }
}

