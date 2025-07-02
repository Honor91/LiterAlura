package com.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String nombreDelAutor;
    private Integer nacimientoDelAutor;
    private Integer fallecimientoDelAutor;
    @ManyToMany(mappedBy = "autor", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Libro> libro = new ArrayList<>();

    public List<Libro> getLibro(){return libro;}

    public void setLibro(List<Libro> libro) {
        this.libro = libro;
    }

    public Integer getId() {return id;}
    public String getNombreDelAutor() {return nombreDelAutor;}
    public Integer getNacimientoDelAutor() {return nacimientoDelAutor;}
    public Integer getFallecimientoDelAutor() {return fallecimientoDelAutor;}
    public List<Libro> getLibros() {return libro;}

    public Autor(String nombreDelAutor,Integer nacimientoDelAutor, Integer fallecimientoDelAutor){
        this.nombreDelAutor = nombreDelAutor;
        this.nacimientoDelAutor = nacimientoDelAutor;
        this.fallecimientoDelAutor = fallecimientoDelAutor;
    }
    public Autor(){

    }

    @Override
    public String toString() {
        String librosStr = (libro==null || libro.isEmpty()) ?
                "ninguno":
                libro.stream()
                        .map(Libro::getTitulo)
                        .reduce((a,b)->a+", "+b)
                        .orElse("");
        return  " Autor: " + nombreDelAutor  + "\n"+
                " Nacimiento: " + nacimientoDelAutor +"\n"+
                " Fallecimiento: " + fallecimientoDelAutor +"\n"+
                " Libros: " + librosStr;
    }


}

