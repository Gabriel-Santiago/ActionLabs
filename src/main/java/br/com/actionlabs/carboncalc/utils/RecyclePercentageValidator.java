package br.com.actionlabs.carboncalc.utils;

import br.com.actionlabs.carboncalc.exceptions.InvalidRecyclePercentageException;
import org.springframework.stereotype.Component;

@Component
public class RecyclePercentageValidator {

    public void validate(double number) {
        if (number < 0 || number > 1){
            throw new InvalidRecyclePercentageException();
        }
    }
}
