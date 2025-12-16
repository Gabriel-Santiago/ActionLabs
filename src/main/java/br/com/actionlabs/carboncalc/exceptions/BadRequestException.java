package br.com.actionlabs.carboncalc.exceptions;

public abstract class BadRequestException extends RuntimeException{
    protected BadRequestException(String message) {
        super(message);
    }
}
