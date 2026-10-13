package ie.schrodingerscode.skillswap.auth.domain;

import java.util.Locale;

import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;

public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email is required");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidEmailException("Email format is incorrect");
        }
        if (value.length() > 255) {
            throw new InvalidEmailException("Email is too long");
        }
    }
}
