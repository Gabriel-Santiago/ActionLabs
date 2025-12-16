package br.com.actionlabs.carboncalc.exceptions;

public class SolidWasteEmissionFactorNotFoundException extends RuntimeException{
    public SolidWasteEmissionFactorNotFoundException(String uf) {
        super("Solid Waste Emission Factor not found for UF: " + uf);
    }
}
