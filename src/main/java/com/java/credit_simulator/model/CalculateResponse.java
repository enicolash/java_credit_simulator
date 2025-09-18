package com.java.credit_simulator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateResponse {
    private List<YearlyInformation> yearlyInformations;
    private String installmentMonthlyAverage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YearlyInformation {
        private Integer year;
        private Double interestRate;
        private String principalAmount;
        private String totalLoanAmount;
        private String installmentMonthly;
        private String installmentYearly;

        public Double getMonthlyInstallment() {
            return Double.parseDouble(this.installmentMonthly);
        }
    }
}

