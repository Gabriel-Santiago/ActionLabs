package br.com.actionlabs.carboncalc.exceptions;

public class InvalidPhoneNumberLengthException extends BadRequestException {
    public InvalidPhoneNumberLengthException() {
        super("This phone number is shorter than 11 characters");
    }
}
