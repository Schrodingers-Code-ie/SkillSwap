package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

// This is the ACTUAL CARD we get back if it's a match - we get:
// 1. The ID and name of this person
// 2. How well he/she match with us (matchingScore)
// 3. Skills HE/SHE can teach US (so skills that satisfy something in our "learn" set) (if any)
// 4. Skills WE can teach HIM/HER (so skills that satisfy something in our "teach" set) (if any) 
public record Match(long matchUserID, String matchUserName, int matchingScore, Set<String> skillsTheyCanTeachMe,
                Set<String> skillsICanTeachThem, Set<String> unmatchedSkillsTheyTeach) {
}
