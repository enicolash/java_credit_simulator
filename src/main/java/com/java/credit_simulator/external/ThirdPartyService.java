package com.java.credit_simulator.external;

import com.java.credit_simulator.model.CalculateRequest;

public interface ThirdPartyService {
    CalculateRequest loadExistingData();
}
