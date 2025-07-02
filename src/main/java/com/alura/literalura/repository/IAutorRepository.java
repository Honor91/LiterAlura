package com.alura.literalura.repository;

import com.alura.literalura.models.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAutorRepository extends JpaRepository<Autor, Integer> {
    Optional<Autor> findByNombreDelAutor(String nombreDelAutor);

}
