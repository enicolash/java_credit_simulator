package com.java.credit_simulator.service.impl;

import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.service.CalculateLoanService;
import com.java.credit_simulator.util.CreditSimulatorUtils;
import com.java.credit_simulator.util.VehicleTypeSpec;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class CalculateLoanServiceImplement implements CalculateLoanService {
    @Override
    public CalculateResponse calculate (CalculateRequest request){
        VehicleTypeSpec vehicleTypeSpec = VehicleTypeSpec.fromString(request.getVehicleType());

        int tenure = request.getLoanTenure();
        double baseInterestRate = vehicleTypeSpec.getBaseInterestRate();

        BigDecimal principalAmount = CreditSimulatorUtils.getPrincipalAmount(
                request.getTotalLoanAmount(),
                request.getDownPayment()
        );

        List<CalculateResponse.YearlyInformation> yearlyInformations = new ArrayList<>();

        for (int year = 1; year <= tenure; year++) {
            log.debug("[calculate] Year : {}, principalAmount : {},  ",year, principalAmount);
            log.debug("[calculate] Year : {}, principalAmount : {},  ",year, principalAmount);

            double currentInterestRate = calculateInterestRate(baseInterestRate, year);
            double currentRateAfterConverted = CreditSimulatorUtils.convertPercentageRateToActualRate(currentInterestRate);

            BigDecimal interestAmount = CreditSimulatorUtils.getInterestAmount(
                    principalAmount,
                    BigDecimal.valueOf(currentRateAfterConverted)
            );
            log.debug("[calculate] Year : {}, InterestAmount : {}", year, interestAmount);

            BigDecimal totalLoanAmount = CreditSimulatorUtils.getTotalLoanAmount(principalAmount,interestAmount);
            log.debug("[calculate] Year : {}, totalLoanAmount : {}", year, totalLoanAmount);

            double installmentMonthlyDivider = CreditSimulatorUtils.getInstallmentMonthlyDivider(year,tenure);
            log.debug("[calculate] Year : {}, installmentMonthlyDivider : {},  ", year, installmentMonthlyDivider);

            BigDecimal installmentMonthly = CreditSimulatorUtils.getInstallmentMonthly(
                    totalLoanAmount,
                    BigDecimal.valueOf(installmentMonthlyDivider));
            log.debug("[calculate] Year : {}, installmentMonthly : {},  ", year, installmentMonthly);


            BigDecimal installmentYearly = CreditSimulatorUtils.getInstallmentYearly(installmentMonthly);
            log.debug("[calculate] Year : {}, installmentYearly : {},  ", year, installmentYearly);

            CalculateResponse.YearlyInformation yearlyInformation = new CalculateResponse.YearlyInformation(
                    year,
                    currentInterestRate,
                    CreditSimulatorUtils.formatBigDecimal(principalAmount),
                    CreditSimulatorUtils.formatBigDecimal(totalLoanAmount),
                    CreditSimulatorUtils.formatBigDecimal(installmentMonthly),
                    CreditSimulatorUtils.formatBigDecimal(installmentYearly)
            );

            yearlyInformations.add(yearlyInformation);

            // Update information for next iteration
            baseInterestRate = currentInterestRate;
            principalAmount = CreditSimulatorUtils.getPrincipalAmount(
                    totalLoanAmount.doubleValue(),
                    installmentYearly.doubleValue()
            );
        }

        BigDecimal installmentMonthlyAverage = calculateAverageInstallmentMonthly(yearlyInformations,tenure);
        log.debug("[calculate] installmentMonthlyAverage : {},  ", installmentMonthlyAverage);

        return CalculateResponse.builder()
                .yearlyInformations(yearlyInformations)
                .installmentMonthlyAverage(CreditSimulatorUtils.formatBigDecimal(installmentMonthlyAverage))
                .build();
    }

    private double calculateInterestRate(double baseRate, int year) {
        double additionalRate = 0;
        boolean isFirstYear = (year - 1) == 0 ;
        boolean isEveryTwoYear = (year - 2) % 2 == 1;

        if(!isFirstYear && !isEveryTwoYear){
            Double yearlyAddtionalRate = CreditSimulatorUtils.getAdditionalYearlyRate();
            additionalRate += yearlyAddtionalRate;
        }

        if(isEveryTwoYear){
            Double twoYearlyAdditionalRate = CreditSimulatorUtils.getTwoYearAdditionalRate();
            additionalRate += twoYearlyAdditionalRate;
        }

        double finalRate = CreditSimulatorUtils.getFinalRate(baseRate,additionalRate);
        double roundedRate = Math.round(finalRate * 100.0) / 100.0;

        log.debug("[calculateInterestRate] Year : {}, Final Rate : {} ", year, finalRate);
        return roundedRate;
    }

    private BigDecimal calculateAverageInstallmentMonthly(List<CalculateResponse.YearlyInformation> yearlyInformations, int tenure) {
        BigDecimal totalInstallmentYearly = yearlyInformations.stream()
                .map(CalculateResponse.YearlyInformation::getInstallmentYearly)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totelMontlyTenure = tenure * 12;
        return totalInstallmentYearly.divide(BigDecimal.valueOf(totelMontlyTenure), RoundingMode.HALF_UP);
    }
}