-- заполнение таблицы mpa
DELETE FROM film_genre;
DELETE FROM likes;
DELETE FROM films;
DELETE FROM mpa;
DELETE FROM friendship;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM reviews;
DELETE FROM reviewLikes;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE mpa ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN reviewId RESTART WITH 1;
ALTER TABLE reviewLikes ALTER COLUMN id RESTART WITH 1;

INSERT INTO mpa(id, name) VALUES (1, 'G');
INSERT INTO mpa(id, name) VALUES (2, 'PG');
INSERT INTO mpa(id, name) VALUES (3, 'PG-13');
INSERT INTO mpa(id, name) VALUES (4, 'R');
INSERT INTO mpa(id, name) VALUES (5, 'NC-17');

-- заполнение таблицы genres
DELETE FROM genres;

INSERT INTO genres (name) VALUES ('Комедия');
INSERT INTO genres (name) VALUES ('Драма');
INSERT INTO genres (name) VALUES ('Мультфильм');
INSERT INTO genres (name) VALUES ('Триллер');
INSERT INTO genres (name) VALUES ('Документальный');
INSERT INTO genres (name) VALUES ('Боевик');
