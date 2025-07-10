package com.example.crudapp.application.dto;

/**
 * Data Transfer Object para Usuario
 * Representa los datos de un usuario que se transfieren entre capas
 * 
 */
public class UsuarioDTO {
    
    private String nombre;
    private String email;
    private String telefono;
    
    /**
     * Constructor por defecto
     */
    public UsuarioDTO() {}
    
    /**
     * Constructor con parámetros
     * 
     * @param nombre nombre del usuario
     * @param email email del usuario
     * @param telefono teléfono del usuario
     */
    public UsuarioDTO(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
    
    /**
     * Obtiene el nombre del usuario
     * 
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Establece el nombre del usuario
     * 
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Obtiene el email del usuario
     * 
     * @return email del usuario
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Establece el email del usuario
     * 
     * @param email email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Obtiene el teléfono del usuario
     * 
     * @return teléfono del usuario
     */
    public String getTelefono() {
        return telefono;
    }
    
    /**
     * Establece el teléfono del usuario
     * 
     * @param telefono teléfono del usuario
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    /**
     * Representación en cadena del objeto UsuarioDTO
     * 
     * @return cadena formateada con los datos del usuario
     */
    @Override
    public String toString() {
        return String.format("UsuarioDTO{nombre='%s', email='%s', telefono='%s'}", 
                           nombre, email, telefono);
    }
}