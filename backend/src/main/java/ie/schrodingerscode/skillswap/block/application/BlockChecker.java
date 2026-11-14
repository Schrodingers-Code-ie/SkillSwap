package ie.schrodingerscode.skillswap.block.application;

/**
 * Small interface other features can use to ask "has either of these two blocked the other?",
 * without reaching into block's repository or database table.
 * Connection uses it so a blocked pair can't send each other swap requests.
 */
public interface BlockChecker {

    boolean isBlockedEitherWay(long userA, long userB);
}
