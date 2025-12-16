package br.com.actionlabs.carboncalc.exceptions;

public class NullUfException extends BadRequestException {
    public NullUfException() {
        super("This UF is null");
    }
}
