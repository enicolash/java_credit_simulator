package com.java.credit_simulator.util;

public enum VehicleTypeSpec {
    MOBIL("Mobil", 8.0),
    MOTOR("Motor", 9.0);

    private final String type;
    private final double baseInterestRate;

    VehicleTypeSpec(String type, double baseInterestRate) {
        this.type = type;
        this.baseInterestRate = baseInterestRate;
    }

    public String getType() {
        return type;
    }

    public double getBaseInterestRate() {
        return baseInterestRate;
    }

    public static VehicleTypeSpec fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        String normalizedValue = value.trim().toUpperCase();
        for (VehicleTypeSpec type : VehicleTypeSpec.values()) {
            if (type.getType().toUpperCase().equals(normalizedValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid vehicle type: " + value);
    }
}

