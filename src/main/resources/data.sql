-- Users
INSERT INTO users (id, email, first_name, last_name, password, role, username) VALUES
(1, 'user1@example.com', 'John', 'Doe', 'password123', 'USER', 'johndoe'),
(2, 'user2@example.com', 'Jane', 'Smith', 'password456', 'ADMIN', 'janesmith');

-- Medications
INSERT INTO medications (id, active, dosage_quantity, dosage_unit, name, notes, user_id) VALUES
(1, b'1', 500, 'mg', 'Paracetamol', 'Take after meals', 1),
(2, b'1', 250, 'mg', 'Ibuprofen', 'Take with water', 2);

-- Posologies
INSERT INTO posologies (id, day_time, doses_number, end_date, frequency_unit, frequency_value, quantity, reminder_message, start_date, medication_id, user_id) VALUES
(1, '08:00:00', 10, '2025-10-01', 'DAYS', 1, 1.0, 'Take one pill every day', '2025-09-20', 1, 1),
(2, '09:00:00', 5, '2025-12-01', 'WEEKS', 1, 2.0, 'Take two pills once a week', '2025-09-20', 2, 2);

-- Doses
INSERT INTO doses (id, is_taken, scheduled_date_time, scheduled_day, taken_time, posology_id, user_id) VALUES
(1, b'0', '2025-09-20 08:00:00', '2025-09-20', NULL, 1, 1),
(2, b'1', '2025-09-21 08:00:00', '2025-09-21', '2025-09-21 08:05:00', 1, 1),
(3, b'0', '2025-09-27 09:00:00', '2025-09-27', NULL, 2, 2);
