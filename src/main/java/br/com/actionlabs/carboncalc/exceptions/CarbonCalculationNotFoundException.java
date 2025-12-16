package br.com.actionlabs.carboncalc.exceptions;

public class CarbonCalculationNotFoundException extends NotFoundException{
    public CarbonCalculationNotFoundException(String id) {
        super("Carbon Calculation not found for id: " + id);
    }
}
