package br.com.actionlabs.carboncalc.exceptions;

public class EnergyEmissionFactorNotFoundException extends NotFoundException{
    public EnergyEmissionFactorNotFoundException(String uf) {
        super("Energy Emission Factor not found for UF: " + uf);
    }
}
