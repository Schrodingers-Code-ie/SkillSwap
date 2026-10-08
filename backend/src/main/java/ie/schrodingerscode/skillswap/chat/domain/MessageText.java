package ie.schrodingerscode.skillswap.chat.domain;

/**
 * The text of a chat message. A value object:
 * 1. It has no identity. It is defined only by its value, so two
 * MessageText("hi") are equal and interchangeable.
 * (A Message, by contrast, has an id: two messages with the same text are still
 * different messages.)
 * 2. It is immutable: it can't change after it is created (record).
 * 3. It validates itself, so an invalid one can never exist.
 */
public record MessageText(String value) {
    /* Maximum number of characters, including spaces */
    public static final int MAX_LENGTH = 2000;

    public MessageText {
        /**
         * First check for null (JSON has no "text" field).
         * Secound check for empty string or only with whitespaces/newline.
         */
        if (value == null || value.isBlank()) {
            throw new InvalidMessageTextException("Message text must not be empty");
        }

        /**
         * Check for character limit.
         * Spaces count towards the limit.
         */
        if (value.length() > MAX_LENGTH) {
            throw new InvalidMessageTextException("Message text must be at most " + MAX_LENGTH + " characters");
        }
    }

}
