-- Insertar usuarios solo si no existen (usando ON CONFLICT)
INSERT INTO usuarios (nombre, email, telefono) VALUES 
('Juan Pérez', 'juan@email.com', '123456789'),
('María García', 'maria@email.com', '987654321'),
('Carlos López', 'carlos@email.com', '456789123')
ON CONFLICT (email) DO NOTHING;

-- Insertar libros solo si no existen (usando ON CONFLICT)
INSERT INTO libros (titulo, autor, isbn, genero, año_publicacion) VALUES 
('Cien años de soledad', 'Gabriel García Márquez', '978-84-376-0494-7', 'Realismo mágico', 1967),
('El Quijote', 'Miguel de Cervantes', '978-84-376-0495-4', 'Clásico', 1605),
('1984', 'George Orwell', '978-84-376-0496-1', 'Distopía', 1949)
ON CONFLICT (isbn) DO NOTHING;

-- Insertar préstamos solo si no existen usuarios y libros
INSERT INTO prestamos (usuario_id, libro_id, fecha_devolucion_esperada) 
SELECT u.id, l.id, '2025-07-18'
FROM usuarios u, libros l 
WHERE u.email = 'juan@email.com' 
  AND l.isbn = '978-84-376-0494-7'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado = 'ACTIVO'
  );

INSERT INTO prestamos (usuario_id, libro_id, fecha_devolucion_esperada) 
SELECT u.id, l.id, '2025-07-20'
FROM usuarios u, libros l 
WHERE u.email = 'maria@email.com' 
  AND l.isbn = '978-84-376-0496-1'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado = 'ACTIVO'
  );

INSERT INTO prestamos (usuario_id, libro_id, fecha_devolucion_esperada) 
SELECT u.id, l.id, '2025-07-15'
FROM usuarios u, libros l 
WHERE u.email = 'carlos@email.com' 
  AND l.isbn = '978-84-376-0495-4'
  AND NOT EXISTS (
    SELECT 1 FROM prestamos p 
    WHERE p.usuario_id = u.id 
      AND p.libro_id = l.id 
      AND p.estado = 'ACTIVO'
  );