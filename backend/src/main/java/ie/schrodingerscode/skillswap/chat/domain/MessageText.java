package ie.schrodingerscode.skillswap.chat.domain;

import ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException;

/**
 * The text of a chat message.
 *
 * <p>A value object: it has no id, so two {@code MessageText("hi")} are equal and interchangeable.
 * It can't change after it is created, and it validates itself, so an invalid one can never exist.
 * This is the only place the text rules live; the request DTO only checks that the field is present.
 *
 * @param value the text, not blank and at most {@link #MAX_LENGTH} characters
 */
public record MessageText(String value) {

    /** Maximum number of characters, spaces and newlines included. */
    public static final int MAX_LENGTH = 2000;

    /**
     * Checks the text rules.
     *
     * @throws InvalidMessageTextException if the text is null (no "text" field in the JSON), empty,
     *                                     only whitespace, or longer than {@link #MAX_LENGTH}
     */
    public MessageText {
        if (value == null || value.isBlank()) {
            throw new InvalidMessageTextException("Message text must not be empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new InvalidMessageTextException("Message text must be at most " + MAX_LENGTH + " characters");
        }
    }

}
