package ie.schrodingerscode.skillswap.connection.domain;

/**
 * Two users, stored with the smaller ID first.
 * So UserPair.of(1, 2) and UserPair.of(2, 1) are equal, and checking for a request between
 * two people takes one lookup, whichever of them sent it.
 *
 * @param first the smaller id
 * @param second the bigger id
 */
public record UserPair(long first, long second) {

    public static UserPair of(long a, long b) {
        return new UserPair(Math.min(a, b), Math.max(a, b));
    }
}
