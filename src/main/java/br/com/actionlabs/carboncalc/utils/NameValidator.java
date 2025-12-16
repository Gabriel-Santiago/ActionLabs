package br.com.actionlabs.carboncalc.utils;

import br.com.actionlabs.carboncalc.exceptions.InvalidNameLengthException;
import br.com.actionlabs.carboncalc.exceptions.NullNameException;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {

    public void validateName(String name) {
        if (name == null) {
            throw new NullNameException();
        }

        if (name.length() < 3) {
            throw new InvalidNameLengthException();
        }
    }
}
