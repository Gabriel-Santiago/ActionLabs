package br.com.actionlabs.carboncalc.exceptions;

public class NullNameException extends BadRequestException {
    public NullNameException() {
        super("This name is null");
    }
}
