package br.com.actionlabs.carboncalc.exceptions;

public class InvalidPhoneNumberLengthException extends BadRequestException {
    public InvalidPhoneNumberLengthException() {
        super("This phone number must be exactly 11 characters");
    }
}
