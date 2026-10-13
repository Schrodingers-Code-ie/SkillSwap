package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

public class DuplicateSkillException extends ConflictException {
    public DuplicateSkillException(String message) {
        super(message);
    }
}