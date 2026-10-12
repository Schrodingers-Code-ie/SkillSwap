package ie.schrodingerscode.skillswap.user.domain;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

public class DuplicateSkillException extends ConflictException {
    public DuplicateSkillException(String message) {
        super(message);
    }
}