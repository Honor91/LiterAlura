package com.alura.literalura.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer bookId;
    private String titulo;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER )
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autor = new ArrayList<>();
    private String languages="";
    private boolean derechosDeAutor;
    private String tipoMedia;
    private Integer cantidadDeDescargas;

    //GETTERS

    public String getIdioms() {
        return languages;
    }

    public void setIdioms(String idioms) {
        this.languages = idioms;
    }

    public Integer getBookId() {return bookId;}
    public Integer getId() {return id;}
    public String getTitulo() {return titulo;}
    public List<Autor> getAutor() {return autor;}
    public boolean isDerechosDeAutor() {return derechosDeAutor;}
    public String getTipoMedia() {return tipoMedia;}
    public Integer getCantidadDeDescargas() {return cantidadDeDescargas;}

    public void setId(Integer id) {this.id = id;}
    public void setBookId(Integer bookId) {this.bookId = bookId;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public void setAutor(List<Autor> autor) {this.autor = autor;}
    public void setDerechosDeAutor(boolean derechosDeAutor) {this.derechosDeAutor = derechosDeAutor;}
    public void setTipoMedia(String tipoMedia) {this.tipoMedia = tipoMedia;}
    public void setCantidadDeDescargas(Integer cantidadDeDescargas) {this.cantidadDeDescargas = cantidadDeDescargas;}

    // CONSTRUCTOR
    public Libro(GutendexResults results){
        this.bookId = results.bookId();
        this.titulo = results.titulo();
        this.autor = new ArrayList<>();
        if (results.autores() != null && !results.autores().isEmpty()){
            for(GutendexAutor autorData : results.autores()){
                Autor autor = new Autor(
                        autorData.nombreDelAutor(),
                        autorData.nacimientoDelAutor(),
                        autorData.fallecimientoDelAutor()
                        );
                this.autor.add(autor);
            }
        }
        this.languages =String.join("-",results.idiomas());
        this.derechosDeAutor = results.derechosDeAutor();
        this.tipoMedia = results.tipoMedia();
        this.cantidadDeDescargas =results.cantidadDeDescargas();
    }
    public Libro(){
    }
    public void agregarAutor(Autor autor){
        if (autor!=null && !this.autor.contains(autor)){
            this.autor.add(autor);
            autor.getLibro().add(this);
        }
    }
    public void eliminarAutor(Autor autor){
       if(autor != null){
           this.autor.remove(autor);
           autor.getLibro().remove(this);
       }
    }



        @Override
    public String toString() {
        String autoresStr = autor.stream()
                .map(Autor::getNombreDelAutor)
                .reduce((a,b)->a+", "+b)
                .orElse("ninguno");
        return  "*******  Book  *******"  +"\n"+
                "Title: " + titulo  + "\n"+
                "Id: " + bookId +"\n"+
                "Author: " + autoresStr +"\n"+
                "Language: " + languages +"\n"+
                "Copyright: " + derechosDeAutor +"\n"+
                "Media Type: " + tipoMedia +"\n"+
                "Downloads: " + cantidadDeDescargas +"\n"+
                "***********************" +"\n";
    }

}
