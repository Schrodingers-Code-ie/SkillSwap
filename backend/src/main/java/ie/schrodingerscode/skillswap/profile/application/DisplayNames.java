package ie.schrodingerscode.skillswap.profile.application;

import java.util.Collection;
import java.util.Map;

/**
 * Small interface other features can use to show users' display names,
 * without reaching into profile's repository or database table.
 * Connection uses it so requests and friends show "Ann Smith" instead of "User 5".
 */
public interface DisplayNames {

    /**
     * All the names in one query.
     *
     * @param userIds the users we need names for
     * @return userId to display name, ids without a profile are left out
     */
    Map<Long, String> displayNamesOf(Collection<Long> userIds);
}
