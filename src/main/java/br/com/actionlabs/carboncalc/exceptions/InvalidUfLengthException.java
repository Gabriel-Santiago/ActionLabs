package br.com.actionlabs.carboncalc.exceptions;

public class InvalidUfLengthException extends BadRequestException {
    public InvalidUfLengthException() {
        super("The state code must be exactly 2 characters long");
    }
}
