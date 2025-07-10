CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATE DEFAULT CURRENT_DATE,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger para usuarios
DROP TRIGGER IF EXISTS trigger_usuarios_fecha_modificacion ON usuarios;
CREATE TRIGGER trigger_usuarios_fecha_modificacion
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Tabla libros
CREATE TABLE IF NOT EXISTS libros (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    genero VARCHAR(50),
    año_publicacion INTEGER CHECK (año_publicacion > 1400 AND año_publicacion <= EXTRACT(YEAR FROM CURRENT_DATE)),
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger para libros
DROP TRIGGER IF EXISTS trigger_libros_fecha_modificacion ON libros;
CREATE TRIGGER trigger_libros_fecha_modificacion
    BEFORE UPDATE ON libros
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Tabla préstamos
CREATE TABLE IF NOT EXISTS prestamos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    libro_id INTEGER NOT NULL,
    fecha_prestamo DATE DEFAULT CURRENT_DATE,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'DEVUELTO', 'VENCIDO')),
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE
);

-- Trigger para préstamos
DROP TRIGGER IF EXISTS trigger_prestamos_fecha_modificacion ON prestamos;
CREATE TRIGGER trigger_prestamos_fecha_modificacion
    BEFORE UPDATE ON prestamos
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Índices para mejor performance
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_libros_isbn ON libros(isbn);
CREATE INDEX IF NOT EXISTS idx_prestamos_usuario_id ON prestamos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_prestamos_libro_id ON prestamos(libro_id);
CREATE INDEX IF NOT EXISTS idx_prestamos_estado ON prestamos(estado);