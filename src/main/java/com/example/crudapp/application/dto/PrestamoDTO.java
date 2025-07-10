package com.example.crudapp.application.dto;

import java.time.LocalDate;

public class PrestamoDTO {
    
    private Long usuarioId;
    private Long libroId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private String estado;
    private String observaciones;
    
    /**
     * Constructor por defecto
     */
    public PrestamoDTO() {}
    
    /**
     * Constructor para crear un nuevo préstamo
     * 
     * @param usuarioId ID del usuario que solicita el préstamo
     * @param libroId ID del libro a prestar
     * @param fechaDevolucionEsperada fecha esperada de devolución
     */
    public PrestamoDTO(Long usuarioId, Long libroId, LocalDate fechaDevolucionEsperada) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.estado = "ACTIVO";
    }
    
    /**
     * Constructor completo
     * 
     * @param usuarioId ID del usuario
     * @param libroId ID del libro
     * @param fechaPrestamo fecha del préstamo
     * @param fechaDevolucionEsperada fecha esperada de devolución
     * @param fechaDevolucionReal fecha real de devolución
     * @param estado estado del préstamo
     * @param observaciones observaciones del préstamo
     */
    public PrestamoDTO(Long usuarioId, Long libroId, LocalDate fechaPrestamo, 
                      LocalDate fechaDevolucionEsperada, LocalDate fechaDevolucionReal,
                      String estado, String observaciones) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }
    
    /**
     * Obtiene el ID del usuario
     * 
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    /**
     * Establece el ID del usuario
     * 
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    /**
     * Obtiene el ID del libro
     * 
     * @return ID del libro
     */
    public Long getLibroId() {
        return libroId;
    }
    
    /**
     * Establece el ID del libro
     * 
     * @param libroId ID del libro
     */
    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }
    
    /**
     * Obtiene la fecha del préstamo
     * 
     * @return fecha del préstamo
     */
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }
    
    /**
     * Establece la fecha del préstamo
     * 
     * @param fechaPrestamo fecha del préstamo
     */
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }
    
    /**
     * Obtiene la fecha esperada de devolución
     * 
     * @return fecha esperada de devolución
     */
    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }
    
    /**
     * Establece la fecha esperada de devolución
     * 
     * @param fechaDevolucionEsperada fecha esperada de devolución
     */
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }
    
    /**
     * Obtiene la fecha real de devolución
     * 
     * @return fecha real de devolución
     */
    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }
    
    /**
     * Establece la fecha real de devolución
     * 
     * @param fechaDevolucionReal fecha real de devolución
     */
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }
    
    /**
     * Obtiene el estado del préstamo
     * 
     * @return estado del préstamo
     */
    public String getEstado() {
        return estado;
    }
    
    /**
     * Establece el estado del préstamo
     * 
     * @param estado estado del préstamo
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene las observaciones del préstamo
     * 
     * @return estado del préstamo
     */
    public String getObservaciones() {
        return observaciones;
    }
    
    /**
     * Establece el estado del préstamo
     * 
     * @param estado estado del préstamo
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    /**
     * Representación en cadena del objeto PrestamoDTO
     * 
     * @return cadena formateada con los datos del préstamo
     */
    @Override
    public String toString() {
        return String.format("PrestamoDTO{usuarioId=%d, libroId=%d, fechaPrestamo=%s, fechaDevolucionEsperada=%s, fechaDevolucionReal=%s, estado='%s'}", 
                           usuarioId, libroId, fechaPrestamo, fechaDevolucionEsperada, fechaDevolucionReal, estado);
    }
}