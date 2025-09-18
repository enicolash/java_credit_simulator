package com.java.credit_simulator.external.impl;

import com.java.credit_simulator.external.ThirdPartyService;
import com.java.credit_simulator.model.CalculateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyServiceImplements implements ThirdPartyService {
    private final RestTemplate restTemplate;

    @Value("${third-party.url}")
    public String API_URL;

    @Override
    public CalculateRequest loadExistingData() {
        try {
            log.info("[loadExistingData] Load data from API: {}", API_URL);
            CalculateRequest request = restTemplate.getForObject(API_URL, CalculateRequest.class);
            if (request != null) {
                log.info("[loadExistingData] Existing data from API : {}", request);
                return request;
            } else {
                log.error("[loadExistingData] Failed load Data Existing from API");
                throw new RuntimeException("Failed load data from API : No data received");
            }

        } catch (Exception e) {
            log.error("[loadExistingData] Error load data from API: {}", e.getMessage());
            throw new RuntimeException("Failed to load data from API");
        }
    }
}

