package br.com.actionlabs.carboncalc.exceptions;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException() {
        super("Invalid email address");
    }
}
