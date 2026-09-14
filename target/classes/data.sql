-- data.sql (ms-vidasalud-appointments)
INSERT INTO atenciones (id, paciente_id, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (1, '11223344-5', 1, 1, 'SOLICITADA', '2026-09-14 09:00:00')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO atenciones (id, paciente_id, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (2, '99887766-K', 2, 2, 'CONFIRMADA', '2026-09-14 09:30:00')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO atenciones (id, paciente_id, prestacion_id, cupo_id, estado, fecha_creacion)
VALUES (3, '55667788-1', 1, 3, 'EN_ESPERA', '2026-09-14 10:00:00')
ON DUPLICATE KEY UPDATE id=id;