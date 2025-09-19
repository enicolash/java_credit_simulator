package com.java.credit_simulator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.credit_simulator.util.IsExisting;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for vehicle loan calculation")
public class CalculateRequest {

    @NotBlank(message = "Vehicle type is required")
    @Schema(description = "Type of vehicle", example = "Mobil", allowableValues = {"Mobil", "Motor"})
    @JsonProperty("vehicleType")
    private String vehicleType;

    @NotBlank(message = "Vehicle condition is required")
    @Schema(description = "Condition of vehicle", example = "Baru", allowableValues = {"Baru", "Bekas"})
    @JsonProperty("vehicleCondition")
    private String vehicleCondition;

    @NotNull(message = "Vehicle year is required")
    @Schema(description = "Year of vehicle manufacture", example = "2025")
    @JsonProperty("vehicleYear")
    private Integer vehicleYear;

    @NotNull(message = "Total loan amount is required")
    @DecimalMin(value = "1000000", message = "Minimum loan amount is 1,000,000")
    @DecimalMax(value = "1000000000", message = "Maximum loan amount is 1,000,000,000")
    @Schema(description = "Total loan amount", example = "500000000", maximum = "1000000000")
    @JsonProperty("totalLoanAmount")
    private Double totalLoanAmount;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 year")
    @Max(value = 6, message = "Maximum tenure is 6 years")
    @Schema(description = "Loan tenure in years", example = "3", minimum = "1", maximum = "6")
    @JsonProperty("loanTenure")
    private Integer loanTenure;

    @NotNull(message = "Down payment amount is required")
    @DecimalMin(value = "0", message = "Down payment cannot be negative")
    @Schema(description = "Down payment amount", example = "200000000", minimum = "0")
    @JsonProperty("downPayment")
    private Double downPayment;

    @Schema(description = "Flag to indicate if using existing data")
    @JsonProperty("isExisting")
    private IsExisting isExisting;
}
