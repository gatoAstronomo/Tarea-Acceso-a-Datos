package com.example.crudapp.presentation.utils;

import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.domain.entities.Prestamo;

import java.util.List;

/**
 * Utilidad para formatear tablas en consola
 * Proporciona métodos para mostrar datos en formato tabular
 * 
 * @author Tu Nombre
 * @version 1.0
 * @since 2025-01-01
 */
public class TableFormatter {
    
    private static final String HORIZONTAL_LINE = "─";
    private static final String VERTICAL_LINE = "│";
    private static final String CORNER_TOP_LEFT = "┌";
    private static final String CORNER_TOP_RIGHT = "┐";
    private static final String CORNER_BOTTOM_LEFT = "└";
    private static final String CORNER_BOTTOM_RIGHT = "┘";
    private static final String JUNCTION_TOP = "┬";
    private static final String JUNCTION_BOTTOM = "┴";
    private static final String JUNCTION_LEFT = "├";
    private static final String JUNCTION_RIGHT = "┤";
    private static final String JUNCTION_CROSS = "┼";
    
    /**
     * Muestra una tabla de usuarios
     * 
     * @param usuarios lista de usuarios a mostrar
     */
    public void mostrarTablaUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios para mostrar.");
            return;
        }
        
        // Calcular anchos de columnas
        int[] anchos = {4, 25, 30, 15}; // ID, Nombre, Email, Teléfono
        
        // Ajustar anchos basado en el contenido
        for (Usuario usuario : usuarios) {
            anchos[0] = Math.max(anchos[0], String.valueOf(usuario.getId()).length());
            anchos[1] = Math.max(anchos[1], usuario.getNombre().length());
            anchos[2] = Math.max(anchos[2], usuario.getEmail().length());
            anchos[3] = Math.max(anchos[3], usuario.getTelefono().length());
        }
        
        // Línea superior
        imprimirLineaHorizontal(anchos, CORNER_TOP_LEFT, JUNCTION_TOP, CORNER_TOP_RIGHT);
        
        // Encabezados
        imprimirFila(new String[]{"ID", "Nombre", "Email", "Teléfono"}, anchos);
        
        // Línea separadora
        imprimirLineaHorizontal(anchos, JUNCTION_LEFT, JUNCTION_CROSS, JUNCTION_RIGHT);
        
        // Datos
        for (Usuario usuario : usuarios) {
            imprimirFila(new String[]{
                String.valueOf(usuario.getId()),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono()
            }, anchos);
        }
        
        // Línea inferior
        imprimirLineaHorizontal(anchos, CORNER_BOTTOM_LEFT, JUNCTION_BOTTOM, CORNER_BOTTOM_RIGHT);
    }
    
    /**
     * Muestra una tabla de libros
     * 
     * @param libros lista de libros a mostrar
     */
    public void mostrarTablaLibros(List<Libro> libros) {
        if (libros.isEmpty()) {
            System.out.println("No hay libros para mostrar.");
            return;
        }
        
        // Calcular anchos de columnas
        int[] anchos = {4, 30, 20, 15, 20, 6, 12}; // ID, Título, Autor, ISBN, Género, Año, Disponible
        
        // Ajustar anchos basado en el contenido
        for (Libro libro : libros) {
            anchos[0] = Math.max(anchos[0], String.valueOf(libro.getId()).length());
            anchos[1] = Math.max(anchos[1], libro.getTitulo().length());
            anchos[2] = Math.max(anchos[2], libro.getAutor().length());
            anchos[3] = Math.max(anchos[3], libro.getIsbn().length());
            anchos[4] = Math.max(anchos[4], libro.getGenero().length());
            anchos[5] = Math.max(anchos[5], String.valueOf(libro.getAñoPublicacion()).length());
        }
        
        // Línea superior
        imprimirLineaHorizontal(anchos, CORNER_TOP_LEFT, JUNCTION_TOP, CORNER_TOP_RIGHT);
        
        // Encabezados
        imprimirFila(new String[]{"ID", "Título", "Autor", "ISBN", "Género", "Año", "Disponible"}, anchos);
        
        // Línea separadora
        imprimirLineaHorizontal(anchos, JUNCTION_LEFT, JUNCTION_CROSS, JUNCTION_RIGHT);
        
        // Datos
        for (Libro libro : libros) {
            imprimirFila(new String[]{
                String.valueOf(libro.getId()),
                truncarTexto(libro.getTitulo(), anchos[1]),
                truncarTexto(libro.getAutor(), anchos[2]),
                libro.getIsbn(),
                truncarTexto(libro.getGenero(), anchos[4]),
                String.valueOf(libro.getAñoPublicacion()),
                libro.getDisponible() ? "Sí" : "No"
            }, anchos);
        }
        
        // Línea inferior
        imprimirLineaHorizontal(anchos, CORNER_BOTTOM_LEFT, JUNCTION_BOTTOM, CORNER_BOTTOM_RIGHT);
    }
    
    /**
     * Muestra una tabla de préstamos
     * 
     * @param prestamos lista de préstamos a mostrar
     */
    public void mostrarTablaPrestamos(List<Prestamo> prestamos) {
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
            return;
        }
        
        // Calcular anchos de columnas
        int[] anchos = {4, 12, 12, 12, 12, 12, 10}; // ID, Usuario ID, Libro ID, Fecha préstamo, Fecha esperada, Fecha real, Estado
        
        // Línea superior
        imprimirLineaHorizontal(anchos, CORNER_TOP_LEFT, JUNCTION_TOP, CORNER_TOP_RIGHT);
        
        // Encabezados
        imprimirFila(new String[]{"ID", "Usuario ID", "Libro ID", "Fecha Prést.", "Fecha Esp.", "Fecha Real", "Estado"}, anchos);
        
        // Línea separadora
        imprimirLineaHorizontal(anchos, JUNCTION_LEFT, JUNCTION_CROSS, JUNCTION_RIGHT);
        
        // Datos
        for (Prestamo prestamo : prestamos) {
            imprimirFila(new String[]{
                String.valueOf(prestamo.getId()),
                String.valueOf(prestamo.getUsuarioId()),
                String.valueOf(prestamo.getLibroId()),
                prestamo.getFechaPrestamo().toString(),
                prestamo.getFechaDevolucionEsperada().toString(),
                prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal().toString() : "No devuelto",
                prestamo.getEstado()
            }, anchos);
        }
        
        // Línea inferior
        imprimirLineaHorizontal(anchos, CORNER_BOTTOM_LEFT, JUNCTION_BOTTOM, CORNER_BOTTOM_RIGHT);
    }
    
    /**
     * Imprime una línea horizontal de la tabla
     * 
     * @param anchos anchos de las columnas
     * @param inicio carácter de inicio
     * @param separador carácter separador
     * @param fin carácter de fin
     */
    private void imprimirLineaHorizontal(int[] anchos, String inicio, String separador, String fin) {
        System.out.print(inicio);
        
        for (int i = 0; i < anchos.length; i++) {
            System.out.print(HORIZONTAL_LINE.repeat(anchos[i] + 2));
            
            if (i < anchos.length - 1) {
                System.out.print(separador);
            }
        }
        
        System.out.println(fin);
    }
    
    /**
     * Imprime una fila de datos
     * 
     * @param datos datos de la fila
     * @param anchos anchos de las columnas
     */
    private void imprimirFila(String[] datos, int[] anchos) {
        System.out.print(VERTICAL_LINE);
        
        for (int i = 0; i < datos.length; i++) {
            String dato = datos[i] != null ? datos[i] : "";
            System.out.print(String.format(" %-" + anchos[i] + "s ", dato));
            System.out.print(VERTICAL_LINE);
        }
        
        System.out.println();
    }
    
    /**
     * Trunca texto si es demasiado largo
     * 
     * @param texto texto a truncar
     * @param maxLength longitud máxima
     * @return texto truncado
     */
    private String truncarTexto(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Muestra una tabla simple con encabezados y datos
     * 
     * @param encabezados encabezados de la tabla
     * @param filas filas de datos
     * @param anchos anchos de las columnas
     */
    public void mostrarTablaGenerica(String[] encabezados, String[][] filas, int[] anchos) {
        if (filas.length == 0) {
            System.out.println("No hay datos para mostrar.");
            return;
        }
        
        // Línea superior
        imprimirLineaHorizontal(anchos, CORNER_TOP_LEFT, JUNCTION_TOP, CORNER_TOP_RIGHT);
        
        // Encabezados
        imprimirFila(encabezados, anchos);
        
        // Línea separadora
        imprimirLineaHorizontal(anchos, JUNCTION_LEFT, JUNCTION_CROSS, JUNCTION_RIGHT);
        
        // Datos
        for (String[] fila : filas) {
            imprimirFila(fila, anchos);
        }
        
        // Línea inferior
        imprimirLineaHorizontal(anchos, CORNER_BOTTOM_LEFT, JUNCTION_BOTTOM, CORNER_BOTTOM_RIGHT);
    }
}