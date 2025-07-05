CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATE DEFAULT CURRENT_DATE
);

CREATE TABLE libros (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    genero VARCHAR(50),
    año_publicacion INTEGER,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE prestamos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    libro_id INTEGER NOT NULL,
    fecha_prestamo DATE DEFAULT CURRENT_DATE,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado VARCHAR(20) DEFAULT 'activo',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (libro_id) REFERENCES libros(id)
);

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

SELECT 
    p.id as prestamo_id,
    u.nombre as usuario,
    l.titulo as libro,
    p.fecha_prestamo,
    p.fecha_devolucion_esperada,
    p.estado
FROM prestamos p
JOIN usuarios u ON p.usuario_id = u.id
JOIN libros l ON p.libro_id = l.id;