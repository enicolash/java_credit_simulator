package com.java.credit_simulator.service;

import com.java.credit_simulator.external.ThirdPartyService;
import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.service.impl.CreditSimulatorServiceImplement;
import com.java.credit_simulator.util.IsExisting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditSimulatorServiceImplementTest {
    @Mock
    private CalculateLoanService calculateLoanService;

    private CreditSimulatorServiceImplement creditSimulatorService;

    @Mock
    private ThirdPartyService thirdPartyService;

    @BeforeEach
    void setUp() {
        creditSimulatorService = CreditSimulatorServiceImplement.builder()
                .calculateLoanService(calculateLoanService)
                .thirdPartyService(thirdPartyService)
                .build();
    }

    @Test
    void calculate_Loan_New_Success() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(100000000.0)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.N)
                .build();

        CalculateResponse expectedResponse = CalculateResponse.builder()
                .yearlyInformations(new ArrayList<>())
                .installmentMonthlyAverage("2441233.33")
                .build();

        when(calculateLoanService.calculate(any(CalculateRequest.class)))
                .thenReturn(expectedResponse);

        CalculateResponse response = creditSimulatorService.calculate(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    void calculate_Loan_Existing_Success() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(100000000.0)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.Y)
                .build();

        CalculateRequest mockThirdPartyResponse = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Baru")
                .vehicleYear(2025)
                .totalLoanAmount(1000000000.00)
                .loanTenure(6)
                .downPayment(500000000.00)
                .build();

        when(thirdPartyService.loadExistingData()).thenReturn(mockThirdPartyResponse);
        CalculateResponse expected = CalculateResponse.builder()
                .yearlyInformations(new ArrayList<>())
                .installmentMonthlyAverage("9309584.29")
                .build();

        when(calculateLoanService.calculate(any(CalculateRequest.class)))
                .thenReturn(expected);

        CalculateResponse actual = creditSimulatorService.calculate(request);
        assertEquals(expected.getInstallmentMonthlyAverage(),
                actual.getInstallmentMonthlyAverage());
    }

    @Test
    void calculate_MissingVehicleType_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(100000000.0)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'vehicleType' in request", exception.getMessage());
    }

    @Test
    void calculate_MissingVehicleCondition_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleYear(2024)
                .totalLoanAmount(100000000.0)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'vehicleCondition' in request", exception.getMessage());
    }

    @Test
    void calculate_MissingVehicleYear_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .totalLoanAmount(200000000.0)
                .loanTenure(3)
                .downPayment(50000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'vehicleYear' in request", exception.getMessage());
    }

    @Test
    void calculate_MissingTotalLoanAmount_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .loanTenure(3)
                .downPayment(25000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'totalLoanAmount' in request", exception.getMessage());
    }

    @Test
    void calculate_MissingLoanTenure_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(200000000.0)
                .downPayment(50000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'loanTenure' in request", exception.getMessage());
        verify(calculateLoanService, never()).calculate(any());
    }

    @Test
    void calculate_MissingDownPayment_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(200000000.0)
                .loanTenure(3)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Missing 'downPayment' in request", exception.getMessage());
        verify(calculateLoanService, never()).calculate(any());
    }

    @Test
    void calculate_InvalidLoanTenure_LessThan1_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(200000000.0)
                .loanTenure(0)
                .downPayment(50000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Loan tenure must be between 1 and 6 years", exception.getMessage());
        verify(calculateLoanService, never()).calculate(any());
    }

    @Test
    void calculate_InvalidLoanTenure_GreaterThan6_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(200000000.0)
                .loanTenure(7)
                .downPayment(50000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Loan tenure must be between 1 and 6 years", exception.getMessage());
    }

    @Test
    void calculate_DownPaymentExceedsLoanAmount_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("mobil")
                .vehicleCondition("bekas")
                .vehicleYear(2024)
                .totalLoanAmount(200000000.0)
                .loanTenure(3)
                .downPayment(250000000.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertTrue(exception.getMessage().contains("Down payment"));
        assertTrue(exception.getMessage().contains("must be below of loan amount"));
    }

    @Test
    void calculate_InvalidLoanAmount_Zero_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("Mobil")
                .vehicleCondition("Bekas")
                .vehicleYear(2024)
                .totalLoanAmount(0.0)
                .loanTenure(3)
                .downPayment(0.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Total loan amount must be > 0 and < 1 Billion", exception.getMessage());
    }

    @Test
    void calculate_InvalidLoanAmount_ExceedsLimit_ThrowsException() {
        CalculateRequest request = CalculateRequest.builder()
                .vehicleType("mobil")
                .vehicleCondition("bekas")
                .vehicleYear(2024)
                .totalLoanAmount(1000000001.0)
                .loanTenure(3)
                .downPayment(250000001.0)
                .isExisting(IsExisting.N)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditSimulatorService.calculate(request)
        );

        assertEquals("Total loan amount must be > 0 and < 1 Billion", exception.getMessage());
    }
}
