package br.com.actionlabs.carboncalc.exceptions;

public class UpperCaseUfValidationException extends BadRequestException {
    public UpperCaseUfValidationException() {
        super("UF must be in uppercase");
    }
}
