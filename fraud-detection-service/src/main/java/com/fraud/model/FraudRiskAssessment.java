package com.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudRiskAssessment implements Serializable {

    private static final long serialVersionUID = 1L;
    private String riskLevel;
    private String details;

}
