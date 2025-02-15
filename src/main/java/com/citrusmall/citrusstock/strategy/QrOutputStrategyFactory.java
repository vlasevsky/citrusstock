package com.citrusmall.citrusstock.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QrOutputStrategyFactory {

    @Autowired
    private Map<String, QrOutputStrategy> strategies;

    public QrOutputStrategy getStrategy(String format) {
        QrOutputStrategy strategy = strategies.get(format.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return strategy;
    }
}