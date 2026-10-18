CREATE TABLE profiles (
    user_id   BIGINT PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    bio       TEXT
);

CREATE TABLE user_teach_skills (
    user_id     BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    skill_id    BIGINT      NOT NULL REFERENCES skills (id),
    proficiency VARCHAR(20) NOT NULL
        CHECK (proficiency IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    PRIMARY KEY (user_id, skill_id)
);

/*
Proficiency for user_learn_skills wasn't mentioned in the requirements, so I didn't add it.
However, I can add it if you want me to, since we agreed that a user can have any 
proficiency level and still want to learn.
*/
CREATE TABLE user_learn_skills (
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills (id),
    PRIMARY KEY (user_id, skill_id)
);