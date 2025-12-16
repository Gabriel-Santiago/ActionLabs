package br.com.actionlabs.carboncalc.exceptions;

public class NullPhoneNumberException extends BadRequestException {
    public NullPhoneNumberException() {
        super("This phone number is null");
    }
}
