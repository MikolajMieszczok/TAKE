INSERT INTO clubs (club_name, wins, draws, loses, manager_name) VALUES
('Manchester City', 0, 5, 1, 'Pep Guardiola'),
('Arsenal', 0, 5, 1, 'Mikel Arteta'),
('Liverpool', 1, 5, 0, 'Arne Slot'),
('Chelsea', 2, 3, 1, 'Enzo Maresca');

INSERT INTO players (first_name, last_name, shirt_nr, club_id) VALUES
('Ederson', 'Moraes', 31, 1),
('Kyle', 'Walker', 2, 1),
('Ruben', 'Dias', 3, 1),
('John', 'Stones', 5, 1),
('Josko', 'Gvardiol', 24, 1),
('Rodri', 'Cascante', 16, 1),
('Kevin', 'De Bruyne', 17, 1),
('Bernardo', 'Silva', 20, 1),
('Phil', 'Foden', 47, 1),
('Jack', 'Grealish', 10, 1),
('Erling', 'Haaland', 9, 1),
('Aaron', 'Ramsdale', 1, 2),
('Ben', 'White', 4, 2),
('William', 'Saliba', 12, 2),
('Gabriel', 'Magalhaes', 6, 2),
('Oleksandr', 'Zinchenko', 35, 2),
('Declan', 'Rice', 41, 2),
('Martin', 'Ødegaard', 8, 2),
('Kai', 'Havertz', 29, 2),
('Bukayo', 'Saka', 7, 2),
('Gabriel', 'Martinelli', 11, 2),
('Gabriel', 'Jesus', 9, 2),

('Alisson', 'Becker', 1, 3),
('Trent', 'Alexander-Arnold', 66, 3),
('Virgil', 'van Dijk', 4, 3),
('Ibrahima', 'Konaté', 5, 3),
('Andrew', 'Robertson', 26, 3),
('Alexis', 'Mac Allister', 10, 3),
('Dominik', 'Szoboszlai', 8, 3),
('Wataru', 'Endo', 3, 3),
('Mohamed', 'Salah', 11, 3),
('Luis', 'Díaz', 7, 3),
('Darwin', 'Núñez', 9, 3),

('Djordje', 'Petrovic', 28, 4),
('Reece', 'James', 24, 4),
('Axel', 'Disasi', 6, 4),
('Benoît', 'Badiashile', 5, 4),
('Marc', 'Cucurella', 3, 4),
('Moises', 'Caicedo', 25, 4),
('Enzo', 'Fernández', 8, 4),
('Conor', 'Gallagher', 23, 4),
('Raheem', 'Sterling', 7, 4),
('Mykhailo', 'Mudryk', 10, 4),
('Christopher', 'Nkunku', 18, 4);

INSERT INTO matches (date_of_matches, goals_cluba, goals_clubb, cluba_id, clubb_id) VALUES
('2025-08-10', 1, 1, 1, 2),  -- Haaland vs Saka
('2025-08-17', 1, 0, 3, 4),  -- Salah + Badiashile OG
('2025-08-24', 0, 0, 1, 3),  -- no goals
('2025-08-31', 0, 1, 2, 4),  -- Sterling
('2025-09-07', 0, 0, 1, 4),  -- no goals
('2025-09-14', 1, 1, 2, 3),  -- Salah + Zinchenko OG
('2026-01-10', 0, 0, 2, 1), -- no goals
('2026-01-17', 0, 0, 4, 3), -- no goals
('2026-01-24', 1, 1, 3, 1), -- Salah & Haaland
('2026-01-31', 0, 0, 4, 2), -- no goals
('2026-02-07', 1, 0, 4, 1), -- Sterling (Chelsea)
('2026-02-14', 1, 1, 3, 2); -- Salah & Ødegaard

INSERT INTO goals (own_goal, match_id, player_id, minute) VALUES
-- Match 1: Man City vs Arsenal (match_id = 1)
(false, 1, 11, 11),  -- Haaland (Man City)
(false, 1, 22, 45),  -- Saka (Arsenal)

-- Match 2: Liverpool vs Chelsea (match_id = 2)
(false, 2, 33, 35),  -- Salah (Liverpool)
(true, 2, 42, 11),   -- Badiashile (Chelsea, own goal)

-- Match 4: Arsenal vs Chelsea (match_id = 4)
(false, 2, 22, 77),  -- Saka again
(false, 4, 44, 91),  -- Sterling

-- Match 6: Arsenal vs Liverpool (match_id = 6)
(true, 6, 26, 6),   -- Zinchenko own goal
(false, 6, 33, 65),  -- Salah

-- Match 9: Liverpool vs Man City (match_id = 9)
(false, 9, 33, 21),  -- Salah
(false, 9, 11, 42),  -- Haaland

-- Match 12: Liverpool vs Arsenal (match_id = 12)
(false, 12, 33, 22), -- Salah
(false, 12, 21, 89); -- Ødegaard