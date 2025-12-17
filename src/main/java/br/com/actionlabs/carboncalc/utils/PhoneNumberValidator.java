package br.com.actionlabs.carboncalc.utils;

import br.com.actionlabs.carboncalc.exceptions.InvalidPhoneNumberException;
import br.com.actionlabs.carboncalc.exceptions.InvalidPhoneNumberLengthException;
import br.com.actionlabs.carboncalc.exceptions.NullPhoneNumberException;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberValidator {

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new NullPhoneNumberException();
        }

        if (phoneNumber.length() != 11) {
            throw new InvalidPhoneNumberLengthException();
        }

        if (!phoneNumber.matches("\\d+")) {
            throw new InvalidPhoneNumberException();
        }
    }
}
