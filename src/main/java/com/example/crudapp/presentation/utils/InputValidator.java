package com.example.crudapp.presentation.utils;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utilidad para validación de entrada de usuario
 * Proporciona métodos para validar y leer diferentes tipos de datos
 * 
 */
public class InputValidator {
    
    //private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    private final Scanner scanner;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{8,15}$");
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Constructor del validador de entrada
     */
    public InputValidator() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Lee un entero dentro del rango especificado
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @param min valor mínimo permitido
     * @param max valor máximo permitido
     * @return entero válido
     */
    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = Integer.parseInt(scanner.nextLine().trim());
                
                if (valor >= min && valor <= max) {
                    return valor;
                }
                
                System.out.println(String.format("Por favor ingrese un número entre %d y %d", min, max));
                
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
    }
    
    /**
     * Lee un Long válido
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return Long válido
     */
    public Long leerLong(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
    }
    
    /**
     * Lee una cadena no vacía
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return cadena no vacía
     */
    public String leerCadenaNoVacia(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            
            if (!valor.isEmpty()) {
                return valor;
            }
            
            System.out.println("Este campo no puede estar vacío");
        }
    }
    
    /**
     * Lee un email válido
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return email válido
     */
    public String leerEmail(String mensaje) {
        while (true) {
            String email = leerCadenaNoVacia(mensaje);
            
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            
            System.out.println("Por favor ingrese un email válido (ejemplo: usuario@dominio.com)");
        }
    }
    
    /**
     * Lee un teléfono válido
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return teléfono válido
     */
    public String leerTelefono(String mensaje) {
        while (true) {
            String telefono = leerCadenaNoVacia(mensaje);
            
            if (PHONE_PATTERN.matcher(telefono).matches()) {
                return telefono;
            }
            
            System.out.println("Por favor ingrese un teléfono válido (8-15 dígitos)");
        }
    }
    
    /**
     * Lee una fecha válida
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return fecha válida
     */
    public LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + " (formato: dd/MM/yyyy): ");
                String fechaStr = scanner.nextLine().trim();
                return LocalDate.parse(fechaStr, DATE_FORMATTER);
                
            } catch (DateTimeParseException e) {
                System.out.println("Por favor ingrese una fecha válida en formato dd/MM/yyyy");
            }
        }
    }
    
    /**
     * Lee una respuesta sí/no
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return true si es sí, false si es no
     */
    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            
            return switch (respuesta) {
                case "s", "si", "sí", "y", "yes" -> true;
                case "n", "no" -> false;
                default -> {
                    System.out.println("Por favor responda 's' para sí o 'n' para no");
                    yield false; // Continuar el bucle
                }
            };
        }
    }
    /**
     * Lee una cadena opcional (puede estar vacía)
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return cadena ingresada (puede ser vacía)
     */
    public String leerCadenaOpcional(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
    
    /**
     * Lee un email opcional
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return email válido o cadena vacía
     */
    public String leerEmailOpcional(String mensaje) {
        while (true) {
            String email = leerCadenaOpcional(mensaje);
            
            if (email.isEmpty() || EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            
            System.out.println("Por favor ingrese un email válido o presione Enter para mantener el actual");
        }
    }
    
    /**
     * Lee un teléfono opcional
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @return teléfono válido o cadena vacía
     */
    public String leerTelefonoOpcional(String mensaje) {
        while (true) {
            String telefono = leerCadenaOpcional(mensaje);
            
            if (telefono.isEmpty() || PHONE_PATTERN.matcher(telefono).matches()) {
                return telefono;
            }
            
            System.out.println("Por favor ingrese un teléfono válido o presione Enter para mantener el actual");
        }
    }
    
    /**
     * Lee un entero opcional
     * 
     * @param mensaje mensaje a mostrar al usuario
     * @param min valor mínimo permitido
     * @param max valor máximo permitido
     * @return entero válido o null si está vacío
     */
    public Integer leerEnteroOpcional(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = leerCadenaOpcional(mensaje);
                
                if (input.isEmpty()) {
                    return null;
                }
                
                int valor = Integer.parseInt(input);
                
                if (valor >= min && valor <= max) {
                    return valor;
                }
                
                System.out.println(String.format("Por favor ingrese un número entre %d y %d", min, max));
                
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            }
        }
    }
    
}
