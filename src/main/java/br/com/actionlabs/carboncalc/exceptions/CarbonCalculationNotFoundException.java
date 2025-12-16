package br.com.actionlabs.carboncalc.exceptions;

public class CarbonCalculationNotFoundException extends RuntimeException{
    public CarbonCalculationNotFoundException(String id) {
        super("Carbon Calculation not found for id: " + id);
    }
}
