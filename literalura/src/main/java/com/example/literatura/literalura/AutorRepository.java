package com.example.literatura.literalura;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByFechaFallecimientoAfter(LocalDate fecha);
    List<Autor> findByFechaFallecimientoIsNull();
}
