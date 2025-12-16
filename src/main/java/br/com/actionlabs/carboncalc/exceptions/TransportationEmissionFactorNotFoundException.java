package br.com.actionlabs.carboncalc.exceptions;

import br.com.actionlabs.carboncalc.enums.TransportationType;

public class TransportationEmissionFactorNotFoundException extends NotFoundException{
    public TransportationEmissionFactorNotFoundException(TransportationType type) {
        super("Transportation Emission Factor not found for transport type: " + type);
    }
}
