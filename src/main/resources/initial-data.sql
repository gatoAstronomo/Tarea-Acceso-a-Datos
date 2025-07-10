-- Insertar usuarios solo si no existen (usando ON CONFLICT)
INSERT INTO usuarios (nombre, email, telefono) VALUES 
('Juan Pérez', 'juan@email.com', '123456789'),
('María García', 'maria@email.com', '987654321'),
('Carlos López', 'carlos@email.com', '456789123'),
('Ana Martínez', 'ana@email.com', '555123456'),
('Pedro Rodríguez', 'pedro@email.com', '666789123')
ON CONFLICT (email) DO NOTHING;

-- Insertar libros solo si no existen (usando ON CONFLICT)
INSERT INTO libros (titulo, autor, isbn, genero, año_publicacion) VALUES 
('Cien años de soledad', 'Gabriel García Márquez', '978-84-376-0494-7', 'Realismo mágico', 1967),
('El Quijote', 'Miguel de Cervantes', '978-84-376-0495-4', 'Clásico', 1605),
('1984', 'George Orwell', '978-84-376-0496-1', 'Distopía', 1949),
('Crónica de una muerte anunciada', 'Gabriel García Márquez', '978-84-376-0497-8', 'Realismo mágico', 1981),
('El principito', 'Antoine de Saint-Exupéry', '978-84-376-0498-5', 'Infantil', 1943),
('Fahrenheit 451', 'Ray Bradbury', '978-84-376-0499-2', 'Ciencia ficción', 1953),
('La metamorfosis', 'Franz Kafka', '978-84-376-0500-5', 'Literatura moderna', 1915)
ON CONFLICT (isbn) DO NOTHING;

-- ✅ CREAR PRÉSTAMOS ACTIVOS Y MARCAR LIBROS COMO NO DISPONIBLES

-- Préstamo 1: Juan toma "Cien años de soledad" (ACTIVO)
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, estado) 
SELECT u.id, l.id, '2025-07-08', '2025-07-18', 'ACTIVO'
FROM usuarios u, libros l 
WHERE u.email = 'juan@email.com' 
  AND l.isbn = '978-84-376-0494-7'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado = 'ACTIVO'
  );

-- Préstamo 2: María toma "1984" (ACTIVO)
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, estado) 
SELECT u.id, l.id, '2025-07-09', '2025-07-20', 'ACTIVO'
FROM usuarios u, libros l 
WHERE u.email = 'maria@email.com' 
  AND l.isbn = '978-84-376-0496-1'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado = 'ACTIVO'
  );

-- Préstamo 3: Carlos toma "El Quijote" (ACTIVO - VENCIDO)
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, estado) 
SELECT u.id, l.id, '2025-07-01', '2025-07-08', 'VENCIDO'
FROM usuarios u, libros l 
WHERE u.email = 'carlos@email.com' 
  AND l.isbn = '978-84-376-0495-4'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado IN ('ACTIVO', 'VENCIDO')
  );

-- ✅ EJEMPLOS DE PRÉSTAMOS DEVUELTOS (historial)
-- Juan devolvió "Crónica de una muerte anunciada"
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado) 
SELECT u.id, l.id, '2025-06-20', '2025-06-30', '2025-06-28', 'DEVUELTO'
FROM usuarios u, libros l 
WHERE u.email = 'juan@email.com' 
  AND l.isbn = '978-84-376-0497-8'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.fecha_prestamo = '2025-06-20'
  );

-- Ana devolvió "El principito"
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado) 
SELECT u.id, l.id, '2025-06-15', '2025-06-25', '2025-06-24', 'DEVUELTO'
FROM usuarios u, libros l 
WHERE u.email = 'ana@email.com' 
  AND l.isbn = '978-84-376-0498-5'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.fecha_prestamo = '2025-06-15'
  );

-- Pedro devolvió "Fahrenheit 451" (tarde - multa)
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado, observaciones) 
SELECT u.id, l.id, '2025-06-01', '2025-06-10', '2025-06-15', 'DEVUELTO', 'Devolución tardía - 5 días de retraso'
FROM usuarios u, libros l 
WHERE u.email = 'pedro@email.com' 
  AND l.isbn = '978-84-376-0499-2'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.fecha_prestamo = '2025-06-01'
  );

-- ✅ ACTUALIZAR DISPONIBILIDAD DE LIBROS SEGÚN PRÉSTAMOS ACTIVOS/VENCIDOS

-- Marcar como NO disponible los libros con préstamos ACTIVOS
UPDATE libros 
SET disponible = false 
WHERE id IN (
    SELECT DISTINCT p.libro_id 
    FROM prestamos p 
    WHERE p.estado IN ('ACTIVO', 'VENCIDO')
);

-- Asegurar que libros sin préstamos activos estén disponibles
UPDATE libros 
SET disponible = true 
WHERE id NOT IN (
    SELECT DISTINCT p.libro_id 
    FROM prestamos p 
    WHERE p.estado IN ('ACTIVO', 'VENCIDO')
) AND disponible = false;

/*
SELECT 
    l.id,
    l.titulo,
    l.disponible,
    COALESCE(p.estado, 'SIN PRÉSTAMO') as estado_prestamo,
    COALESCE(u.nombre, 'Nadie') as usuario_actual,
    CASE 
        WHEN p.fecha_devolucion_esperada < CURRENT_DATE AND p.estado = 'ACTIVO' THEN 'VENCIDO'
        WHEN p.estado = 'ACTIVO' THEN 'VIGENTE'
        WHEN p.estado = 'DEVUELTO' THEN 'HISTÓRICO'
        ELSE 'DISPONIBLE'
    END as situacion
FROM libros l
LEFT JOIN prestamos p ON l.id = p.libro_id AND p.estado IN ('ACTIVO', 'VENCIDO')
LEFT JOIN usuarios u ON p.usuario_id = u.id
ORDER BY l.id;
*/