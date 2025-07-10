package com.example.crudapp.application.dto;

/**
 * Data Transfer Object para Libro
 * Representa los datos de un libro que se transfieren entre capas
 * 
 */

 public class LibroDTO {
    
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private Integer añoPublicacion;
    private Boolean disponible;
    
    /**
     * Constructor por defecto
     */
    public LibroDTO() {}
    
    /**
     * Constructor con parámetros principales
     * 
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param genero género del libro
     * @param añoPublicacion año de publicación
     */
    public LibroDTO(String titulo, String autor, String isbn, String genero, Integer añoPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.añoPublicacion = añoPublicacion;
        this.disponible = true; // Por defecto disponible
    }
    
    /**
     * Constructor completo
     * 
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param genero género del libro
     * @param añoPublicacion año de publicación
     * @param disponible disponibilidad del libro
     */
    public LibroDTO(String titulo, String autor, String isbn, String genero, Integer añoPublicacion, Boolean disponible) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.añoPublicacion = añoPublicacion;
        this.disponible = disponible;
    }
    
    /**
     * Obtiene el título del libro
     * 
     * @return título del libro
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Establece el título del libro
     * 
     * @param titulo título del libro
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    /**
     * Obtiene el autor del libro
     * 
     * @return autor del libro
     */
    public String getAutor() {
        return autor;
    }
    
    /**
     * Establece el autor del libro
     * 
     * @param autor autor del libro
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }
    
    /**
     * Obtiene el ISBN del libro
     * 
     * @return ISBN del libro
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Establece el ISBN del libro
     * 
     * @param isbn ISBN del libro
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /**
     * Obtiene el género del libro
     * 
     * @return género del libro
     */
    public String getGenero() {
        return genero;
    }
    
    /**
     * Establece el género del libro
     * 
     * @param genero género del libro
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    /**
     * Obtiene el año de publicación del libro
     * 
     * @return año de publicación
     */
    public Integer getAñoPublicacion() {
        return añoPublicacion;
    }
    
    /**
     * Establece el año de publicación del libro
     * 
     * @param añoPublicacion año de publicación
     */
    public void setAñoPublicacion(Integer añoPublicacion) {
        this.añoPublicacion = añoPublicacion;
    }
    
    /**
     * Obtiene la disponibilidad del libro
     * 
     * @return true si está disponible, false en caso contrario
     */
    public Boolean getDisponible() {
        return disponible;
    }
    
    /**
     * Establece la disponibilidad del libro
     * 
     * @param disponible disponibilidad del libro
     */
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
    
    /**
     * Representación en cadena del objeto LibroDTO
     * 
     * @return cadena formateada con los datos del libro
     */
    @Override
    public String toString() {
        return String.format("LibroDTO{titulo='%s', autor='%s', isbn='%s', genero='%s', añoPublicacion=%d, disponible=%s}", 
                           titulo, autor, isbn, genero, añoPublicacion, disponible);
    }
}