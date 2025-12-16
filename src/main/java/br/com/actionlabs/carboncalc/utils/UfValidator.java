package br.com.actionlabs.carboncalc.utils;

import br.com.actionlabs.carboncalc.exceptions.InvalidUfLengthException;
import br.com.actionlabs.carboncalc.exceptions.NullUfException;
import br.com.actionlabs.carboncalc.exceptions.UpperCaseUfValidationException;
import org.springframework.stereotype.Component;

@Component
public class UfValidator {

    public void validateUf(String uf) {
        if (uf == null) {
            throw new NullUfException();
        }

        if (uf.length() != 2) {
            throw new InvalidUfLengthException();
        }

        if (!uf.equals(uf.toUpperCase())) {
            throw new UpperCaseUfValidationException();
        }
    }
}
