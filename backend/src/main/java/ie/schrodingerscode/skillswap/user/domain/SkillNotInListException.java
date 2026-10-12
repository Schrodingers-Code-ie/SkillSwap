package ie.schrodingerscode.skillswap.user.domain;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

public class SkillNotInListException extends NotFoundException {
    public SkillNotInListException(String message) {
        super(message);
    }
}