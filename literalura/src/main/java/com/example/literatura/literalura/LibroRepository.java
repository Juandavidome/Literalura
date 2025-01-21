package com.example.literatura.literalura;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Métodos de búsqueda personalizados si es necesario
}

