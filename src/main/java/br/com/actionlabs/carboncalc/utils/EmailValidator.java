package br.com.actionlabs.carboncalc.utils;

import br.com.actionlabs.carboncalc.exceptions.EmailAlreadyExists;
import br.com.actionlabs.carboncalc.exceptions.InvalidEmailException;
import br.com.actionlabs.carboncalc.repository.CarbonCalculationRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailValidator {

    private final CarbonCalculationRepository carbonCalculationRepository;

    public EmailValidator(CarbonCalculationRepository carbonCalculationRepository) {
        this.carbonCalculationRepository = carbonCalculationRepository;
    }

    public void validateEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new InvalidEmailException();
        }

        if (carbonCalculationRepository.existsByEmail(email)) {
            throw new EmailAlreadyExists();
        }
    }
}
