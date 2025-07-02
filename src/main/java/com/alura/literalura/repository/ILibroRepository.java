package com.alura.literalura.repository;

import com.alura.literalura.models.Autor;
import com.alura.literalura.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ILibroRepository extends JpaRepository<Libro, Integer> {

    List<Libro> findTop5ByOrderByCantidadDeDescargasDesc();
    Optional<Libro> findByBookId(Integer bookId);
    @Query(
            value = "SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.libro WHERE a.nombreDelAutor ILIKE %:nombreDelAutor%"
    )
    List<Autor> librosPorAutores(@Param("nombreDelAutor") String librosPorAutores);
    @Query(
            value = "SELECT l FROM Libro l LEFT JOIN FETCH l.autor WHERE l.languages ILIKE %:idioma%"
    )
    List<Libro> findByIdioms(String idioma);
}

//"SELECT a FROM Autor a \n" +
//        "WHERE a.nombreDelAutor ILIKE %:nombreDelAutor%"