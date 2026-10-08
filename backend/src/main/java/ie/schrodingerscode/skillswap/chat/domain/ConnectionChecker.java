package ie.schrodingerscode.skillswap.chat.domain;

/**
 * STUB, replaced by SCRUM-29 interface after pull request
 * Asks the connections feature whether two users are connected.
 * Chat only asks and never decides.
 * Until S4 is merged, a stub implements it from SCRUM-53.
 */
public interface ConnectionChecker {
    boolean areConnected(long userA, long userB);

}
