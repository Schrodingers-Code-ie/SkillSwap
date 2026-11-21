package ie.schrodingerscode.skillswap.auth.domain;

import java.util.Locale;

import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;

/**
 * An email address that is already checked and normalised.
 *
 * <p>If you have an {@code Email} object, it is valid: not empty, in the name@domain.tld format, at
 * most 255 characters and in lower case with no spaces around it. Lower case means "Bob@Mail.com" and
 * "bob@mail.com" are the same account.
 *
 * @param value the cleaned email text
 */
public record Email(String value) {
    /**
     * Trims, lower-cases and checks the email.
     *
     * @param value the email as typed
     * @throws InvalidEmailException if the email is empty, in the wrong format or too long
     */
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
