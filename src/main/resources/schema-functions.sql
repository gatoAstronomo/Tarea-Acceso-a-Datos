-- Función para actualizar fecha_modificacion
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_modificacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Función para actualizar préstamos vencidos
CREATE OR REPLACE FUNCTION actualizar_prestamos_vencidos()
RETURNS INTEGER AS $$
DECLARE
    v_contador INTEGER;
BEGIN
    -- Actualizar estados de préstamos vencidos
    UPDATE prestamos
    SET estado = 'VENCIDO'
    WHERE estado = 'ACTIVO'
      AND fecha_devolucion_esperada < CURRENT_DATE
      AND fecha_devolucion_real IS NULL;
    
    -- Obtener el número de filas afectadas
    GET DIAGNOSTICS v_contador = ROW_COUNT;
    
    -- Retornar el contador para que lo use el método Java
    RETURN v_contador;
END;
$$ LANGUAGE plpgsql;