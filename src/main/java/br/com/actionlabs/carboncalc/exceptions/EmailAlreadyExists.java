package br.com.actionlabs.carboncalc.exceptions;

public class EmailAlreadyExists extends BadRequestException {
    public EmailAlreadyExists(){
        super("Email already exists");
    }
}
