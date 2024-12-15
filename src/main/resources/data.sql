CREATE TABLE IF NOT EXISTS Fares (
    id SERIAL PRIMARY KEY,
    start_zone INT NOT NULL,
    end_zone INT NOT NULL,
    is_peak BOOLEAN NOT NULL,
    fare DECIMAL(10, 2) NOT NULL
);

INSERT INTO Fares (start_zone, end_zone, is_peak, fare) VALUES
(1, 1, TRUE, 2.50),
(1, 1, FALSE, 2.00),
(1, 2, TRUE, 3.00),
(1, 2, FALSE, 2.50),
(2, 2, TRUE, 2.00),
(2, 2, FALSE, 1.80),
(2, 3, TRUE, 3.20),
(2, 3, FALSE, 2.80),
(3, 3, TRUE, 2.50),
(3, 3, FALSE, 2.30),
(1, 3, TRUE, 4.00),
(1, 3, FALSE, 3.50),
(3, 1, TRUE, 4.00),
(3, 1, FALSE, 3.50);
