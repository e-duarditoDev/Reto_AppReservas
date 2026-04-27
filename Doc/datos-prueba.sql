-- =============================================================
-- DATOS DE PRUEBA — AppEventos
-- Base de datos: retoappbbc_db
-- =============================================================
-- IMPORTANTE: El APILoginManager tiene ddl-auto=create,
-- lo que significa que borra y recrea las tablas en cada arranque.
-- Ejecutar este script DESPUES de que ambas APIs hayan arrancado
-- al menos una vez (para que Hibernate cree el schema).
-- =============================================================
-- Contraseña de todos los usuarios de prueba: 1234
-- Hash BCrypt(10) para "1234":
--   $2a$10$A6XUfkDw9YJ/71dABst6aeM4mBFYjNi1wlypuWvcyUeEa6XeHgfZe
-- Si el login falla, regenera el hash en: https://bcrypt-generator.com (Rounds: 10)
-- =============================================================

USE retoappbbc_db;

-- -------------------------------------------------------------
-- PERFILES
-- -------------------------------------------------------------
INSERT INTO perfiles (nombre) VALUES
    ('ROLE_ADMIN'),
    ('CLIENTE');

-- -------------------------------------------------------------
-- USUARIOS  (password = "1234" hasheada con BCrypt 10 rounds)
-- -------------------------------------------------------------
INSERT INTO usuario (username, password, email, fecha_registro, nombre, apellidos, enabled) VALUES
    ('admin',  '$2a$10$A6XUfkDw9YJ/71dABst6aeM4mBFYjNi1wlypuWvcyUeEa6XeHgfZe', 'admin@test.com',  CURDATE(), 'Admin',   'Sistema',  1),
    ('juan',   '$2a$10$A6XUfkDw9YJ/71dABst6aeM4mBFYjNi1wlypuWvcyUeEa6XeHgfZe', 'juan@test.com',   CURDATE(), 'Juan',    'García',   1),
    ('maria',  '$2a$10$A6XUfkDw9YJ/71dABst6aeM4mBFYjNi1wlypuWvcyUeEa6XeHgfZe', 'maria@test.com',  CURDATE(), 'María',   'López',    1),
    ('carlos', '$2a$10$A6XUfkDw9YJ/71dABst6aeM4mBFYjNi1wlypuWvcyUeEa6XeHgfZe', 'carlos@test.com', CURDATE(), 'Carlos',  'Martínez', 1);

-- -------------------------------------------------------------
-- USUARIO_PERFILES  (admin → ROLE_ADMIN, resto → CLIENTE)
-- -------------------------------------------------------------
INSERT INTO usuario_perfiles (username, id_perfil)
    SELECT 'admin', id_perfil FROM perfiles WHERE nombre = 'ROLE_ADMIN';

INSERT INTO usuario_perfiles (username, id_perfil)
    SELECT 'juan', id_perfil FROM perfiles WHERE nombre = 'CLIENTE';

INSERT INTO usuario_perfiles (username, id_perfil)
    SELECT 'maria', id_perfil FROM perfiles WHERE nombre = 'CLIENTE';

INSERT INTO usuario_perfiles (username, id_perfil)
    SELECT 'carlos', id_perfil FROM perfiles WHERE nombre = 'CLIENTE';

-- -------------------------------------------------------------
-- TIPOS
-- -------------------------------------------------------------
INSERT INTO tipos (nombre, descripcion) VALUES
    ('Boda',         'Celebración de enlace matrimonial con banquete y actividades'),
    ('Cumpleaños',   'Celebración de aniversario personal con decoración temática'),
    ('Conferencia',  'Evento corporativo con ponencias y networking'),
    ('Concierto',    'Actuación musical en directo');

-- -------------------------------------------------------------
-- EVENTOS
-- -------------------------------------------------------------
INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, precio, aforo, estado, destacado, id_tipo)
SELECT
    'Gran Boda de Primavera',
    'Celebración íntima en jardín con decoración floral y menú de temporada.',
    '2026-06-15 18:00:00', '2026-06-16 02:00:00',
    2500.00, 100, 'ACTIVO', 'S',
    id_tipo FROM tipos WHERE nombre = 'Boda';

INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, precio, aforo, estado, destacado, id_tipo)
SELECT
    'Fiesta Cumpleaños VIP',
    'Celebración exclusiva con photocall, DJ y cena de gala.',
    '2026-07-20 20:00:00', '2026-07-21 03:00:00',
    800.00, 50, 'ACTIVO', 'S',
    id_tipo FROM tipos WHERE nombre = 'Cumpleaños';

INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, precio, aforo, estado, destacado, id_tipo)
SELECT
    'Conferencia Tech 2026',
    'Jornada de ponencias sobre inteligencia artificial y desarrollo web.',
    '2026-09-10 09:00:00', '2026-09-10 18:00:00',
    150.00, 200, 'ACTIVO', 'N',
    id_tipo FROM tipos WHERE nombre = 'Conferencia';

INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, precio, aforo, estado, destacado, id_tipo)
SELECT
    'Concierto de Jazz al Atardecer',
    'Velada musical con los mejores artistas de jazz nacional.',
    '2026-08-05 20:30:00', '2026-08-05 23:30:00',
    45.00, 300, 'ACTIVO', 'N',
    id_tipo FROM tipos WHERE nombre = 'Concierto';

INSERT INTO eventos (nombre, descripcion, fecha_inicio, fecha_fin, precio, aforo, estado, destacado, id_tipo)
SELECT
    'Boda de Otoño',
    'Boda íntima con decoración otoñal en finca rural.',
    '2026-10-03 17:00:00', '2026-10-04 01:00:00',
    3200.00, 80, 'CANCELADO', 'N',
    id_tipo FROM tipos WHERE nombre = 'Boda';

-- -------------------------------------------------------------
-- RESERVAS de prueba
-- -------------------------------------------------------------
INSERT INTO reservas (username, fecha_reserva, precio_venta, id_evento)
SELECT 'juan', NOW(), precio, id_evento
FROM eventos WHERE nombre = 'Gran Boda de Primavera';

INSERT INTO reservas (username, fecha_reserva, precio_venta, id_evento)
SELECT 'juan', NOW(), precio, id_evento
FROM eventos WHERE nombre = 'Conferencia Tech 2026';

INSERT INTO reservas (username, fecha_reserva, precio_venta, id_evento)
SELECT 'maria', NOW(), precio, id_evento
FROM eventos WHERE nombre = 'Gran Boda de Primavera';

INSERT INTO reservas (username, fecha_reserva, precio_venta, id_evento)
SELECT 'maria', NOW(), precio, id_evento
FROM eventos WHERE nombre = 'Concierto de Jazz al Atardecer';
