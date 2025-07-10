package com.example;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.crypto.Data;

import org.junit.Test;

import com.example.crudapp.application.dto.PrestamoDTO;
import com.example.crudapp.application.services.PrestamoService;
import com.example.crudapp.domain.entities.Prestamo;
import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.repositories.LibroRepository;
import com.example.crudapp.domain.repositories.PrestamoRepository;
import com.example.crudapp.infrastructure.database.Database;
import com.example.crudapp.infrastructure.repositories.LibroRepositoryImpl;
import com.example.crudapp.infrastructure.repositories.PrestamoRepositoryImpl;
import com.example.crudapp.infrastructure.repositories.UsuarioRepositoryImpl;
import com.example.crudapp.infrastructure.transactions.TransactionManager;

/**
 * Unit test for simple App.
 */
public class AppTest {
    // Test de concurrencia
    @Test
    public void testCrearPrestamosConcurrentes() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger exitos = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);
        PrestamoRepository prestamoRepository = new PrestamoRepositoryImpl();
        UsuarioRepositoryImpl usuarioRepository = new UsuarioRepositoryImpl();
        LibroRepository libroRepository = new LibroRepositoryImpl();
        TransactionManager transactionManager = new TransactionManager(Database.getInstance());

        PrestamoService prestamoService = new PrestamoService(prestamoRepository, usuarioRepository, libroRepository, transactionManager);

        // Dos hilos intentan prestar el mismo libro simultáneamente
        for (int i = 0; i < 2; i++) {
            final long usuarioId = i + 1;
            executor.submit(() -> {
                try {
                    PrestamoDTO dto = new PrestamoDTO();
                    dto.setUsuarioId(usuarioId);
                    dto.setLibroId((long)1); // ← MISMO LIBRO
                    dto.setFechaDevolucionEsperada(LocalDate.now().plusDays(7));

                    prestamoService.crearPrestamo(dto);
                    exitos.incrementAndGet();

                } catch (Exception e) {
                    errores.incrementAndGet();
                    System.out.printf("Préstamo rechazado correctamente: %s", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // ✅ ACID: Solo UNO debe tener éxito
        assertEquals(1, exitos.get());
        assertEquals(1, errores.get());

        // ✅ Verificar estado consistente
        List<Prestamo> prestamosActivos = prestamoRepository.findPrestamosActivosByLibroId(Database.getInstance().getConnection(),1L);
        assertEquals(1, prestamosActivos.size());
    }
}
