package com.example.crudapp.domain.entities;

import java.util.Objects;

public class Libro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private Integer añoPublicacion;
    private Boolean disponible;
    
    public Libro() {}
    
    public Libro(String titulo, String autor, String isbn, String genero, Integer añoPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.añoPublicacion = añoPublicacion;
        this.disponible = true;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public Integer getAñoPublicacion() { return añoPublicacion; }
    public void setAñoPublicacion(Integer añoPublicacion) { this.añoPublicacion = añoPublicacion; }
    
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(id, libro.id) && Objects.equals(isbn, libro.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }
    
    @Override
    public String toString() {
        return String.format("Libro{id=%d, titulo='%s', autor='%s', isbn='%s', genero='%s', año=%d, disponible=%s}",
                id, titulo, autor, isbn, genero, añoPublicacion, disponible);
    }
}