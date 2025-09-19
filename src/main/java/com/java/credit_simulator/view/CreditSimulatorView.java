package com.java.credit_simulator.view;

import com.java.credit_simulator.external.ThirdPartyService;
import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;
import com.java.credit_simulator.service.CreditSimulatorService;
import com.java.credit_simulator.util.CreditSimulatorUtils;
import com.java.credit_simulator.util.IsExisting;
import com.java.credit_simulator.util.VehicleConditionSpec;
import com.java.credit_simulator.util.VehicleTypeSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditSimulatorView implements CommandLineRunner {

    private final CreditSimulatorService creditSimulatorService;
    private final ThirdPartyService thirdPartyService;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<CalculateResponse> savedSheets = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {

        // Check if file input is provided
        if (args.length > 0) {
            processFileInput(args[0]);
        } else {
            showCommand();
        }
    }

    private void processFileInput(String filename) {
        try {
            log.info("Processing file input: {}", filename);
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));

            String vehicleType = fileReader.readLine().trim();
            String vehicleCondition = fileReader.readLine().trim();
            Integer vehicleYear = Integer.parseInt(fileReader.readLine().trim());
            Double totalLoanAmount = Double.parseDouble(fileReader.readLine().trim());
            Integer loanTenure = Integer.parseInt(fileReader.readLine().trim());
            Double downPayment = Double.parseDouble(fileReader.readLine().trim());

            fileReader.close();

            CalculateRequest request = CalculateRequest.builder()
                    .vehicleType(vehicleType)
                    .vehicleCondition(vehicleCondition)
                    .vehicleYear(vehicleYear)
                    .totalLoanAmount(totalLoanAmount)
                    .loanTenure(loanTenure)
                    .downPayment(downPayment)
                    .isExisting(IsExisting.N)
                    .build();

            CalculateResponse response = creditSimulatorService.calculate(request);
            showCalculationResult(request, response);

        } catch (Exception e) {
            System.err.println("Error processing file input: " + e.getMessage());
        }
    }

    private void showCommand() throws IOException {
        System.out.println("\n------ CREDIT SIMULATOR ------");
        System.out.println("Welcome to Credit Simulator");
        System.out.println("Type 'show' to see available commands");
        System.out.println("Type 'exit' to quit\n");

        String input;
        while (true) {
            System.out.print("Answer : ");
            input = reader.readLine().trim().toLowerCase();

            switch (input) {
                case "show":
                    showCommands();
                    break;
                case "1":
                case "calculate":
                    calculateLoan();
                    break;
                case "2":
                case "load":
                    loadExistingAndCalculate();
                    break;
                case "3":
                case "save":
                    saveSheet();
                    break;
                case "4":
                case "list":
                    listSavedSheets();
                    break;
                case "exit":
                    System.out.println("Thank you for using Credit Simulator.");
                    return;
                default:
                    System.out.println("Unknown command. Type 'show' to see available commands.");
            }
        }
    }

    private void showCommands() {
        System.out.println("\n------ AVAILABLE COMMANDS ------");
        System.out.println("1. calculate - Calculate new vehicle loan");
        System.out.println("2. load      - Load existing calculation from third party service");
        System.out.println("3. save      - Save current calculation to sheet");
        System.out.println("4. list      - List all data in sheets");
        System.out.println("show         - Show the command list");
        System.out.println("exit         - Exit application");
        System.out.println("------------------------------------");
    }

    private void calculateLoan() throws IOException {
        try {
            System.out.println("\n---- NEW LOAN CALCULATION -----");

            String vehicleType = getVehicleType();
            String vehicleCondition = getVehicleCondition();
            Integer vehicleYear = getVehicleYear(vehicleCondition);
            Double totalLoanAmount = getTotalLoanAmount();
            Integer loanTenure = getLoanTenure();
            Double downPayment = getDownPayment(totalLoanAmount, vehicleCondition);

            CalculateRequest request = CalculateRequest.builder()
                    .vehicleType(vehicleType)
                    .vehicleCondition(vehicleCondition)
                    .vehicleYear(vehicleYear)
                    .totalLoanAmount(totalLoanAmount)
                    .loanTenure(loanTenure)
                    .downPayment(downPayment)
                    .isExisting(IsExisting.N)
                    .build();

            CalculateResponse response = creditSimulatorService.calculate(request);
            showCalculationResult(request, response);

        } catch (Exception e) {
            System.err.println("Error during calculation: " + e.getMessage());
        }
    }

    private void loadExistingAndCalculate() {
        try {
            System.out.println("\n---- LOAD EXISTING --------");
            System.out.println("Load data from web service.....");

            CalculateRequest request = thirdPartyService.loadExistingData();
            request.setIsExisting(IsExisting.Y);

            System.out.println("Load existing dat success!");
            System.out.println("Vehicle Type: " + request.getVehicleType());
            System.out.println("Vehicle Condition: " + request.getVehicleCondition());
            System.out.println("Vehicle Year: " + request.getVehicleYear());
            System.out.println("Total Loan Amount: " + request.getTotalLoanAmount());
            System.out.println("Loan Tenure: " + request.getLoanTenure() + " years");
            System.out.println("Down Payment: " + request.getDownPayment());

            CalculateResponse response = creditSimulatorService.calculate(request);
            showCalculationResult(request, response);

        } catch (Exception e) {
            System.err.println("Error loading existing calculation: " + e.getMessage());
        }
    }

    private void saveSheet() {
        if (savedSheets.isEmpty()) {
            System.out.println("No calculation results to save. Please perform a calculation first.");
            return;
        }

        CalculateResponse lastResult = savedSheets.get(savedSheets.size() - 1);
        String filename = "calculation_" + System.currentTimeMillis() + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("=== MONTHLY INSTALLMENTS ===\n");
            for (CalculateResponse.YearlyInformation yearInfo : lastResult.getYearlyInformations()) {
                writer.write(String.format("Year %d: %s/month, Interest Rate: %.1f%%\n",
                        yearInfo.getYear(),
                        yearInfo.getMonthlyInstallment(),
                        yearInfo.getInterestRate()));
            }

            System.out.println("Calculation saved to: " + filename);

        } catch (IOException e) {
            System.err.println("Error saving sheet: " + e.getMessage());
        }
    }

    private void listSavedSheets() {
        if (savedSheets.isEmpty()) {
            System.out.println("No saved calculation sheets.");
            return;
        }

        System.out.println("\n------------ SAVED CALCULATION SHEETS ------------");
        for (int i = 0; i < savedSheets.size(); i++) {
            CalculateResponse sheet = savedSheets.get(i);
            for (int year = 0; year < sheet.getYearlyInformations().size(); year++) {
                System.out.printf("  Year %d: %.0f/month, Interest Rate: %.1f%%\n",
                        year + 1,
                        sheet.getYearlyInformations().get(year).getMonthlyInstallment(),
                        sheet.getYearlyInformations().get(year).getInterestRate());
            }
        }
        System.out.println("----------------------------------------------------------\n");
    }

    private String getVehicleType() throws IOException {
        while (true) {
            System.out.print("Enter vehicle type (Motor/Mobil): ");
            String input = reader.readLine().trim();

            try {
                VehicleTypeSpec.fromString(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid vehicle type. Please enter 'Motor' or 'Mobil'.");
            }
        }
    }

    private String getVehicleCondition() throws IOException {
        while (true) {
            System.out.print("Enter vehicle condition (Bekas/Baru): ");
            String input = reader.readLine().trim();

            try {
                VehicleConditionSpec.fromString(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid vehicle condition. Please enter 'Bekas' or 'Baru'.");
            }
        }
    }

    private Integer getVehicleYear(String vehicleCondition) throws IOException {
        while (true) {
            System.out.print("Enter vehicle year (4 digits): ");
            try {
                Integer year = Integer.parseInt(reader.readLine().trim());

                if (year > Year.now().getValue()) {
                    System.out.println("Please enter a valid year (Valid year until " + Year.now().getValue() + ")");
                    continue;
                }

                if (vehicleCondition.equalsIgnoreCase("Baru") && year < (Year.now().getValue() - 1)) {
                    System.out.println("New vehicle year cannot be less than " + (Year.now().getValue() - 1));
                    continue;
                }

                return year;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid 4-digit year.");
            }
        }
    }

    private Double getTotalLoanAmount() throws IOException {
        while (true) {
            System.out.print("Enter total loan amount (max 1 billion): ");
            try {
                Double amount = Double.parseDouble(reader.readLine().trim());

                if (amount <= 0 || amount > 1_000_000_000) {
                    System.out.println("Total loan amount must be > 0 and <= 1 billion");
                    continue;
                }

                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Integer getLoanTenure() throws IOException {
        while (true) {
            System.out.print("Enter loan tenure (1-6 years): ");
            try {
                Integer tenure = Integer.parseInt(reader.readLine().trim());

                if (tenure < 1 || tenure > 6) {
                    System.out.println("Loan tenure must be between 1 and 6 years.");
                    continue;
                }

                return tenure;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Double getDownPayment(Double totalLoanAmount, String vehicleCondition) throws IOException {
        VehicleConditionSpec conditionSpec = VehicleConditionSpec.fromString(vehicleCondition);
        double minDownPayment = CreditSimulatorUtils.getMinimumDownPayment(totalLoanAmount,
                conditionSpec.getMinimumDownPaymentPercentage());

        while (true) {
            System.out.printf("Enter down payment (minimum %s - %.0f%%): ",
                    String.format("%.2f",minDownPayment),
                    conditionSpec.getMinimumDownPaymentPercentage() * 100);
            try {
                Double downPayment = Double.parseDouble(reader.readLine().trim());

                if (downPayment < minDownPayment) {
                    System.out.printf("Down payment must be at least %s\n", minDownPayment);
                    continue;
                }

                if (downPayment >= totalLoanAmount) {
                    System.out.println("Down payment must be less than total loan amount.");
                    continue;
                }

                return downPayment;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void showCalculationResult(CalculateRequest request, CalculateResponse response) {
        System.out.println("\n--------------- CALCULATION RESULT ---------------");
        System.out.println();

        System.out.println("----------- MONTHLY INSTALLMENTS ----------------");
        for (CalculateResponse.YearlyInformation yearInfo : response.getYearlyInformations()) {
            System.out.printf("Year %d: %.2f/month, Interest Rate: %.1f%%\n",
                    yearInfo.getYear(),
                    yearInfo.getMonthlyInstallment(),
                    yearInfo.getInterestRate());
        }
        System.out.println("--------------------------------\n");

        // Save to memory for potential sheet saving
        savedSheets.add(response);
    }
}
