INSERT INTO clubs (club_name, club_record, manager_name) VALUES
('Manchester City', 0, 'Pep Guardiola'),
('Arsenal', 0, 'Mikel Arteta'),
('Liverpool', 0, 'Arne Slot'),
('Chelsea', 0, 'Enzo Maresca');

INSERT INTO players (assists, first_name, last_name, shirt_nr, club_id) VALUES
(3, 'Ederson', 'Moraes', 31, 1),
(2, 'Kyle', 'Walker', 2, 1),
(1, 'Ruben', 'Dias', 3, 1),
(0, 'John', 'Stones', 5, 1),
(1, 'Josko', 'Gvardiol', 24, 1),
(4, 'Rodri', '', 16, 1),
(7, 'Kevin', 'De Bruyne', 17, 1),
(6, 'Bernardo', 'Silva', 20, 1),
(9, 'Phil', 'Foden', 47, 1),
(3, 'Jack', 'Grealish', 10, 1),
(8, 'Erling', 'Haaland', 9, 1),

(0, 'Aaron', 'Ramsdale', 1, 2),
(2, 'Ben', 'White', 4, 2),
(1, 'William', 'Saliba', 12, 2),
(0, 'Gabriel', 'Magalhaes', 6, 2),
(2, 'Oleksandr', 'Zinchenko', 35, 2),
(5, 'Declan', 'Rice', 41, 2),
(7, 'Martin', 'Ødegaard', 8, 2),
(3, 'Kai', 'Havertz', 29, 2),
(10, 'Bukayo', 'Saka', 7, 2),
(4, 'Gabriel', 'Martinelli', 11, 2),
(6, 'Gabriel', 'Jesus', 9, 2),

(1, 'Alisson', 'Becker', 1, 3),
(8, 'Trent', 'Alexander-Arnold', 66, 3),
(0, 'Virgil', 'van Dijk', 4, 3),
(0, 'Ibrahima', 'Konaté', 5, 3),
(3, 'Andrew', 'Robertson', 26, 3),
(2, 'Alexis', 'Mac Allister', 10, 3),
(5, 'Dominik', 'Szoboszlai', 8, 3),
(1, 'Wataru', 'Endo', 3, 3),
(11, 'Mohamed', 'Salah', 11, 3),
(6, 'Luis', 'Díaz', 7, 3),
(7, 'Darwin', 'Núñez', 9, 3),

(0, 'Djordje', 'Petrovic', 28, 4),
(3, 'Reece', 'James', 24, 4),
(1, 'Axel', 'Disasi', 6, 4),
(0, 'Benoît', 'Badiashile', 5, 4),
(2, 'Marc', 'Cucurella', 3, 4),
(4, 'Moises', 'Caicedo', 25, 4),
(3, 'Enzo', 'Fernández', 8, 4),
(6, 'Conor', 'Gallagher', 23, 4),
(5, 'Raheem', 'Sterling', 7, 4),
(2, 'Mykhailo', 'Mudryk', 10, 4),
(4, 'Christopher', 'Nkunku', 18, 4);

INSERT INTO matches (date_of_matches, gameweek, goals_cluba, goals_clubb, cluba_id, clubb_id) VALUES
('2025-08-10', 1, 0, 0, 1, 2),
('2025-08-17', 2, 0, 0, 3, 4),
('2025-08-24', 3, 0, 0, 1, 3),
('2025-08-31', 4, 0, 0, 2, 4),
('2025-09-07', 5, 0, 0, 1, 4),
('2025-09-14', 6, 0, 0, 2, 3),
('2026-01-10', 20, 0, 0, 2, 1),
('2026-01-17', 21, 0, 0, 4, 3),
('2026-01-24', 22, 0, 0, 3, 1),
('2026-01-31', 23, 0, 0, 4, 2),
('2026-02-07', 24, 0, 0, 4, 1),
('2026-02-14', 25, 0, 0, 3, 2);

INSERT INTO goals (own_goal, match_id, player_id) VALUES
-- Match 1: Man City vs Arsenal (match_id = 1)
(false, 1, 11),  -- Haaland (Man City)
(false, 1, 22),  -- Saka (Arsenal)

-- Match 2: Liverpool vs Chelsea (match_id = 2)
(false, 2, 33),  -- Salah (Liverpool)
(true, 2, 42),   -- Badiashile (Chelsea, own goal)

-- Match 4: Arsenal vs Chelsea (match_id = 4)
(false, 2, 22),  -- Saka again
(false, 4, 44),  -- Sterling

-- Match 6: Arsenal vs Liverpool (match_id = 6)
(true, 6, 26),   -- Zinchenko own goal
(false, 6, 33),  -- Salah

-- Match 9: Liverpool vs Man City (match_id = 9)
(false, 9, 33),  -- Salah
(false, 9, 11),  -- Haaland

-- Match 12: Liverpool vs Arsenal (match_id = 12)
(false, 12, 33), -- Salah
(false, 12, 21); -- Ødegaard