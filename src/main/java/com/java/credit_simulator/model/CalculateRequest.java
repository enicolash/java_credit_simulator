package com.java.credit_simulator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.credit_simulator.util.IsExisting;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateRequest {

    @NotBlank(message = "Vehicle type is required")
    @JsonProperty("vehicleType")
    private String vehicleType;

    @NotBlank(message = "Vehicle condition is required")
    @JsonProperty("vehicleCondition")
    private String vehicleCondition;

    @NotNull(message = "Vehicle year is required")
    @JsonProperty("vehicleYear")
    private Integer vehicleYear;

    @NotNull(message = "Total loan amount is required")
    @DecimalMin(value = "1000000", message = "Minimum loan amount is 1,000,000")
    @DecimalMax(value = "1000000000", message = "Maximum loan amount is 1,000,000,000")
    @JsonProperty("totalLoanAmount")
    private Double totalLoanAmount;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 year")
    @Max(value = 6, message = "Maximum tenure is 6 years")
    @JsonProperty("loanTenure")
    private Integer loanTenure;

    @NotNull(message = "Down payment amount is required")
    @DecimalMin(value = "0", message = "Down payment cannot be negative")
    @JsonProperty("downPayment")
    private Double downPayment;

    @JsonProperty("isExisting")
    private IsExisting isExisting;
}
