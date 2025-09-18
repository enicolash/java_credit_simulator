package com.java.credit_simulator.service.impl;

import com.java.credit_simulator.external.ThirdPartyService;
import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.service.CalculateLoanService;
import com.java.credit_simulator.service.CreditSimulatorService;
import com.java.credit_simulator.util.CreditSimulatorUtils;
import com.java.credit_simulator.util.IsExisting;
import com.java.credit_simulator.util.VehicleConditionSpec;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Year;
@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class CreditSimulatorServiceImplement implements CreditSimulatorService {
    private final CalculateLoanService calculateLoanService;

    private final ThirdPartyService thirdPartyService;

    @Override
    public CalculateResponse calculate(CalculateRequest request) {
        log.info("[calculate] REQ : {} ", request);

        validate(request);

        if (isLoadExisting(request)) {
            request = thirdPartyService.loadExistingData();
        }


        CalculateResponse response = calculateLoanService.calculate(request);
        log.info("[calculate] RES : {} ", response);
        return response;
    }

    private void validate(CalculateRequest request) {
        try {
            validateMandatoryRequest(request);
            validateVehicleYear(request);
            validateLoanTenure(request);
            validateDownPayment(request);
            validateLoanAmount(request);
        } catch (IllegalArgumentException err) {
            log.error("[validate] ERR : {}", err.getMessage());
            throw err;
        }
    }

    private void validateMandatoryRequest(CalculateRequest request) {
        if (StringUtils.isBlank(request.getVehicleType())) {
            throw new IllegalArgumentException("Missing 'vehicleType' in request");
        }

        if (StringUtils.isBlank(request.getVehicleCondition())) {
            throw new IllegalArgumentException("Missing 'vehicleCondition' in request");
        }

        if (request.getVehicleYear() == null) {
            throw new IllegalArgumentException("Missing 'vehicleYear' in request");
        }

        if (request.getTotalLoanAmount() == null) {
            throw new IllegalArgumentException("Missing 'totalLoanAmount' in request");
        }

        if (request.getLoanTenure() == null) {
            throw new IllegalArgumentException("Missing 'loanTenure' in request");
        }

        if (request.getDownPayment() == null) {
            throw new IllegalArgumentException("Missing 'downPayment' in request");
        }
        if (request.getIsExisting() == null) {
            throw new IllegalArgumentException("Missing 'isExisting' in request");
        }
    }

    private void validateVehicleYear(CalculateRequest request) {
        if (request.getVehicleCondition().toString().equals(VehicleConditionSpec.BARU.getCondition())) {
            int currentYear = Year.now().getValue();
            if (request.getVehicleYear() < (currentYear - 1)) {
                throw new IllegalArgumentException(
                        String.format("New vehicle year cannot be less than %d", currentYear - 1));
            }
        }
    }

    private void validateLoanTenure(CalculateRequest request) {
        if (request.getLoanTenure() < 1 || request.getLoanTenure() > 6) {
            throw new IllegalArgumentException("Loan tenure must be between 1 and 6 years");
        }
    }

    private void validateDownPayment(CalculateRequest request) {
        if (request.getDownPayment() > request.getTotalLoanAmount()) {
            throw new IllegalArgumentException(
                    String.format("Down payment (Rp %.2f) must be below of loan amount (Rp %.2f)",
                            request.getDownPayment(), request.getTotalLoanAmount()));
        }

        VehicleConditionSpec vehicleConditionSpec = VehicleConditionSpec.fromString(request.getVehicleCondition());

        double minDownPayment = CreditSimulatorUtils.getMinimumDownPayment(request.getTotalLoanAmount(),
                vehicleConditionSpec.getMinimumDownPaymentPercentage());

        if (request.getDownPayment() < minDownPayment) {
            throw new IllegalArgumentException(
                    String.format("Down payment must be at least %.0f%% of loan amount (Rp %.2f)",
                            vehicleConditionSpec.getMinimumDownPaymentPercentage() * 100, minDownPayment));
        }


    }

    private void validateLoanAmount(CalculateRequest request) {
        if (request.getTotalLoanAmount() <= 0 || request.getTotalLoanAmount() > 1_000_000_000) {
            throw new IllegalArgumentException("Total loan amount must be > 0 and < 1 Billion");
        }
    }

    private boolean isLoadExisting(CalculateRequest request) {
        return request.getIsExisting().toString().equals(IsExisting.Y.toString());
    }
}