INSERT IGNORE INTO atenciones (id, paciente_id, rut, nombre_paciente, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (1, 'paciente1@dominio.com', '11223344-5', 'Juan Pérez', 1, 1, 'SOLICITADA', '2026-09-19 09:00:00')
ON DUPLICATE KEY UPDATE rut=VALUES(rut), nombre_paciente=VALUES(nombre_paciente), paciente_id=VALUES(paciente_id);

INSERT IGNORE INTO atenciones (id, paciente_id, rut, nombre_paciente, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (2, 'paciente2@dominio.com', '99887766-K', 'María González', 2, 2, 'CONFIRMADA', '2026-09-19 09:30:00')
ON DUPLICATE KEY UPDATE rut=VALUES(rut), nombre_paciente=VALUES(nombre_paciente), paciente_id=VALUES(paciente_id);

INSERT IGNORE INTO atenciones (id, paciente_id, rut, nombre_paciente, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (3, 'paciente3@dominio.com', '55667788-1', 'Carlos Silva', 1, 3, 'EN_ESPERA', '2026-09-19 10:00:00')
ON DUPLICATE KEY UPDATE rut=VALUES(rut), nombre_paciente=VALUES(nombre_paciente), paciente_id=VALUES(paciente_id);