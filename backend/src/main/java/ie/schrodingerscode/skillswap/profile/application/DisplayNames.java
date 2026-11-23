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
     * Display names for a group of users, loaded in one query.
     *
     * @param userIds the users to look up
     * @return user id to display name. Ids without a profile are left out
     */
    Map<Long, String> displayNamesOf(Collection<Long> userIds);
}
