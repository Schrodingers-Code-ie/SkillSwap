package ie.schrodingerscode.skillswap.block.application;

/**
 * Small interface other features can use to ask "has either of these two blocked the other?",
 * without reaching into block's repository or database table.
 * Connection uses it so a blocked pair can't send each other swap requests.
 */
public interface BlockChecker {

    /**
     * Checks if there is a block between two users, whoever placed it.
     *
     * @param userA the ID of one user
     * @param userB the ID of the other user
     * @return true if A blocked B or B blocked A
     */
    boolean isBlockedEitherWay(long userA, long userB);
}
