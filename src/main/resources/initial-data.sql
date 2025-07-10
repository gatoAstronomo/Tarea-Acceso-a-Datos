INSERT INTO usuarios (nombre, email, telefono) VALUES 
('Juan Pérez', 'juan@email.com', '123456789'),
('María García', 'maria@email.com', '987654321'),
('Carlos López', 'carlos@email.com', '456789123');

INSERT INTO libros (titulo, autor, isbn, genero, año_publicacion) VALUES 
('Cien años de soledad', 'Gabriel García Márquez', '978-84-376-0494-7', 'Realismo mágico', 1967),
('El Quijote', 'Miguel de Cervantes', '978-84-376-0495-4', 'Clásico', 1605),
('1984', 'George Orwell', '978-84-376-0496-1', 'Distopía', 1949);

INSERT INTO prestamos (usuario_id, libro_id, fecha_devolucion_esperada) VALUES 
(1, 1, '2025-07-18'),
(2, 3, '2025-07-20'),
(3, 2, '2025-07-15');
