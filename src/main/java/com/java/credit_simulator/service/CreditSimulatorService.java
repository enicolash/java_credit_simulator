package com.java.credit_simulator.service;

import com.java.credit_simulator.model.CalculateRequest;
import com.java.credit_simulator.model.CalculateResponse;

public interface CreditSimulatorService {
    CalculateResponse calculate (CalculateRequest calculateRequest);

}
