package br.com.actionlabs.carboncalc.exceptions;

public class InvalidRecyclePercentageException extends BadRequestException {
    public InvalidRecyclePercentageException() {
        super("Invalid Recycle Percentage. The percentage must be between 0 and 1.");
    }
}
