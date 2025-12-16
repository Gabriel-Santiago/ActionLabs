package br.com.actionlabs.carboncalc.dto;

public record ErrorResponseDTO(
        boolean success,
        String message
) {
}
