CREATE TABLE skills (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO skills (name) VALUES
    ('Guitar'),
    ('Piano'),
    ('Singing'),
    ('Drums'),
    ('Music Production'),
    ('Spanish'),
    ('French'),
    ('Irish'),
    ('German'),
    ('Japanese'),
    ('Java'),
    ('Python'),
    ('JavaScript'),
    ('SQL'),
    ('Web Design'),
    ('Baking'),
    ('Meal Prep'),
    ('Italian Cuisine'),
    ('Vegetarian Cooking'),
    ('Cocktail Making'),
    ('Yoga'),
    ('Running'),
    ('Weight Training'),
    ('Climbing'),
    ('Swimming');