package com.leliuk.utils;

import java.util.Collection;

public final class MathUtils {

    public static double sum(Collection<Double> toSum) {
        return toSum.stream()
                .reduce(0.0, Double::sum);
    }

    public static double localPriority(Collection<Double> priorities) {
        double multiplied = priorities.stream()
                .reduce(1.0, (l, r) -> l * r);
        return Math.pow(multiplied, 1.0 / priorities.size());
    }

    public static double normalizedLocalPriority(double localPriority, Collection<Double> localPriorities) {
        double localPrioritiesSum = localPriorities.stream()
                .reduce(0.0, Double::sum);
        return normalizedLocalPriority(localPriority, localPrioritiesSum);
    }

    public static double normalizedLocalPriority(double localPriority, double localPrioritySum) {
        return localPriority / localPrioritySum;
    }

    public static double lambda(double normalizedLocalPriority, Collection<Double> priorities) {
        return lambda(sum(priorities), normalizedLocalPriority);
    }

    public static double lambda(double prioritySum, double normalizedLocalPriority) {
        return prioritySum * normalizedLocalPriority;
    }

    public static double consistencyIndex(Collection<Double> lambdas) {
        int itemsQuantity = lambdas.size();
        if (itemsQuantity < 1) {
            return Double.NaN;
        }
        if (itemsQuantity == 1) {
            return 1.0;
        }
        double lambdaSum = sum(lambdas);
        return (lambdaSum - itemsQuantity) / (itemsQuantity - 1);
    }

    private MathUtils() {
        throw new IllegalStateException("MathUtils cannot be instantiated!");
    }
}
