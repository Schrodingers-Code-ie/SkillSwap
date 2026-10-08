package ie.schrodingerscode.skillswap.connection.domain;

/**
 * Two users, stored with the smaller ID first.
 * So UserPair.of(1, 2) and UserPair.of(2, 1) are equal, which makes
 * "a request already exists between these two people, in either direction" one check.
 */
public record UserPair(long first, long second) {

    public static UserPair of(long a, long b) {
        return new UserPair(Math.min(a, b), Math.max(a, b));
    }
}
