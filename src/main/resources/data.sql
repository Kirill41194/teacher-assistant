-- Очистка таблиц (опционально)
DELETE FROM lesson_generation;
DELETE FROM student;
DELETE FROM tutor;
DELETE FROM topic;
DELETE FROM subject;

-- Создаем тестового репетитора
INSERT INTO tutor (tutor_id, full_name, email, password_hash)
VALUES (1, 'Иванов Иван Иванович', 'ivanov@test.com', 'hash123')
    ON CONFLICT (email) DO UPDATE SET
    full_name = EXCLUDED.full_name,
                               password_hash = EXCLUDED.password_hash;

-- Создаем тестового студента С РЕПЕТИТОРОМ
INSERT INTO student (student_id, full_name, level, notes, tutor_id)
VALUES (1, 'Петров Петр Петрович', 'BEGINNER', 'Новичок в математике', 1)
    ON CONFLICT (student_id) DO UPDATE SET
    full_name = EXCLUDED.full_name,
                                    level = EXCLUDED.level,
                                    notes = EXCLUDED.notes,
                                    tutor_id = EXCLUDED.tutor_id;

-- Создаем тестовый предмет
INSERT INTO subject (subject_id, name)
VALUES (1, 'Математика')
    ON CONFLICT (subject_id) DO UPDATE SET
    name = EXCLUDED.name;

-- Создаем тестовую тему
INSERT INTO topic (topic_id, subject, name, description)
VALUES (1, 'Математика', 'Квадратные уравнения', 'Решение уравнений вида ax² + bx + c = 0')
    ON CONFLICT (topic_id) DO UPDATE SET
    subject = EXCLUDED.subject,
                                  name = EXCLUDED.name,
                                  description = EXCLUDED.description;