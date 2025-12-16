package br.com.actionlabs.carboncalc.exceptions;

public class InvalidPhoneNumberException extends BadRequestException {
    public InvalidPhoneNumberException() {
        super("Invalid phone number. The phone number contains non-numeric characters.");
    }
}
