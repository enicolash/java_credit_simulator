package com.java.credit_simulator.util;

public enum VehicleConditionSpec {
    BARU("Baru", 0.35),
    BEKAS("Bekas", 0.25);

    private final String condition;
    private final double minimumDownPaymentPercentage;

    VehicleConditionSpec(String condition, double minimumDownPaymentPercentage) {
        this.condition = condition;
        this.minimumDownPaymentPercentage = minimumDownPaymentPercentage;
    }

    public String getCondition() {
        return condition;
    }

    public double getMinimumDownPaymentPercentage() {
        return minimumDownPaymentPercentage;
    }

    public static VehicleConditionSpec fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Vehicle condition cannot be null");
        }

        String normalizedValue = value.trim().toUpperCase();

        for (VehicleConditionSpec vehicleConditionSpec : VehicleConditionSpec.values()) {
            if (vehicleConditionSpec.getCondition().toUpperCase().equals(normalizedValue)) {
                return vehicleConditionSpec;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle condition: " + value);
    }
}

