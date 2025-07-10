package com.example.crudapp.domain.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Prestamo {
    private Long id;
    private Long usuarioId;
    private Long libroId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private String estado;
    
    // Entidades relacionadas (para joins)
    private Usuario usuario;
    private Libro libro;
    
    public Prestamo() {}
    
    public Prestamo(Long usuarioId, Long libroId, LocalDate fechaDevolucionEsperada) {
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.estado = "activo";
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public Long getLibroId() { return libroId; }
    public void setLibroId(Long libroId) { this.libroId = libroId; }
    
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    
    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) { 
        this.fechaDevolucionEsperada = fechaDevolucionEsperada; 
    }
    
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { 
        this.fechaDevolucionReal = fechaDevolucionReal; 
    }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    
    // Métodos de negocio
    public boolean isVencido() {
        return "activo".equals(estado) && 
               fechaDevolucionEsperada != null && 
               LocalDate.now().isAfter(fechaDevolucionEsperada);
    }
    
    public void devolver() {
        this.fechaDevolucionReal = LocalDate.now();
        this.estado = "devuelto";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestamo prestamo = (Prestamo) o;
        return Objects.equals(id, prestamo.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Prestamo{id=%d, usuarioId=%d, libroId=%d, fechaPrestamo=%s, fechaDevEsperada=%s, fechaDevReal=%s, estado='%s'}",
                id, usuarioId, libroId, fechaPrestamo, fechaDevolucionEsperada, fechaDevolucionReal, estado);
    }
}