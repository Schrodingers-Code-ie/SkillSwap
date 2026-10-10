package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

// An interface used for the STUB 
// But it's the SAME INTERFACE that will be used for the proper implementation, that reads info from the database
// TODO: discuss what methods we need to implement in this contract - what exactly are we reading from the database? Maybe some specific methods will make it easier/more efficient to calculate the matching score?
public interface SkillSwapUsersReader {
    List<SkillSwapUser> findAll();
}
