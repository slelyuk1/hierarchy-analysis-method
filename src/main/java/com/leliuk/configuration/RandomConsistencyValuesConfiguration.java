package com.leliuk.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Configuration
public class RandomConsistencyValuesConfiguration {
    private static final List<Double> DEFAULT_RANDOM_CONSISTENCY = Arrays.asList(0.0, 0.0, 0.58, 0.9, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49);
    private List<Double> randomConsistencyValues;

    public RandomConsistencyValuesConfiguration() {
        randomConsistencyValues = Collections.unmodifiableList(DEFAULT_RANDOM_CONSISTENCY);
    }
}
