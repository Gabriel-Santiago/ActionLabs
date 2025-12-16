package br.com.actionlabs.carboncalc.exceptions;

public class InvalidNameLengthException extends BadRequestException {
    public InvalidNameLengthException() {
        super("This name is shorter than 3 characters");
    }
}
