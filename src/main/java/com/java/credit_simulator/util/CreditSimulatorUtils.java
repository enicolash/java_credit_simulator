package com.java.credit_simulator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditSimulatorUtils {
    private static final double ADDITIONAL_YEARLY_RATE  = 0.1;

    private static final double TWO_YEAR_ADDITIONAL_RATE = 0.5;
    public static BigDecimal getPrincipalAmount(double totalLoanAmount, double downPayment) {
        return BigDecimal.valueOf(totalLoanAmount - downPayment);
    }

    public static double getMinimumDownPayment(double totalLoanAmount,double minDownpaymentSpec) {
        return totalLoanAmount * minDownpaymentSpec;
    }

    public static double getAdditionalYearlyRate(){
        return ADDITIONAL_YEARLY_RATE;
    }

    public static double getTwoYearAdditionalRate(){
        return TWO_YEAR_ADDITIONAL_RATE;
    }

    public static double getFinalRate(double baseRate, double additionalRate){
        return baseRate + additionalRate;
    }

    public static double convertPercentageRateToActualRate(double percentageRate){
        return percentageRate / 100;
    }

    public static BigDecimal getInterestAmount(BigDecimal principalAmount, BigDecimal rate){
        return principalAmount.multiply(rate);
    }

    public static BigDecimal getTotalLoanAmount(BigDecimal principalAmount, BigDecimal interestAmount){
        return principalAmount.add(interestAmount);
    }

    public static double getInstallmentMonthlyDivider(int year, int tenure){
        return (tenure * 12) - ((year - 1) * 12);
    }

    public static BigDecimal getInstallmentMonthly(BigDecimal totalLoanAmount,BigDecimal month){
        return totalLoanAmount.divide(month, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getInstallmentYearly(BigDecimal installmentMonthly){
        return installmentMonthly.multiply(BigDecimal.valueOf(12));
    }
}
