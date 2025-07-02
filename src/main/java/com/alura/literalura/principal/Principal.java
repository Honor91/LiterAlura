package com.alura.literalura.principal;

import com.alura.literalura.models.*;

import com.alura.literalura.repository.IAutorRepository;
import com.alura.literalura.repository.ILibroRepository;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.service.Request;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {
    private final String URL_BASE = "https://gutendex.com/books/"; //
    Request myRequest = new Request();
    private List<GutendexResponse> gutendexResponse;
    ConvierteDatos converter = new ConvierteDatos();
    private Scanner keyboard = new Scanner(System.in);
    private Optional<Libro> libroBuscado;
    private ILibroRepository repositorio;
    private IAutorRepository autorRepositorio;


    public Principal(ILibroRepository repository, IAutorRepository autorRepository){
        this.repositorio = repository;
        this.autorRepositorio = autorRepository;
    }

    public void muestraMenu(){
        var option = -1;
        while (option != 0){
            var menu = """
                    1.- Busca tu libro favorito
                    2.- Busqueda exacta por ID
                    3.- Top 5 libros
                    4.- listar libros por Autor
                    5.- listar libros por Idioma
                    9.- Listar los libros guardados
                    0.- Salir
                    """;
            System.out.println(menu);
            option = keyboard.nextInt();
            keyboard.nextLine();

            switch (option){
                case 1:
                    busquedaAproximada();
                    break;
                case 2:
                    busquedaExactaPorID();
                    break;
                case 3:
                    top5MasDescargados();
                    break;
                case 4:
                    listarAutoresRegistrados();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 9:
                    mostrarLibrosGuardados();
                    break;
                case 0:
                    System.out.println("Cerrando la Aplicacion");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }


    public List<Libro> generalData(GutendexResponse datos,String json){
        datos = converter.getData(json,GutendexResponse.class);

        List<Libro> myBook = datos.results().stream()
                .flatMap(r-> (  r.autores().isEmpty() ? Stream.of((Autor) null): r.autores().stream())
                                .map(t->new Libro(r)))
                .collect(Collectors.toList());
        return myBook;
    }
    private void busquedaAproximada() {
        System.out.println("Ingresa el nombre de tu libro favorito para hacer una busqueda aproximada");
        var myfavoriteBook = keyboard.nextLine();
        var json = myRequest.getDatafromAPI(URL_BASE+"?search="+myfavoriteBook.replace(" ","%20"));
        GutendexResponse datos = converter.getData(json,GutendexResponse.class);
        Optional<GutendexResults> optionalResults = datos.results().stream().findFirst();
        if (optionalResults.isPresent()){
            System.out.println("Se encontraron las siguientes coincidencias");
            generalData(datos,json).forEach(b-> System.out.printf("""
                Titulo: %s | ID: %d  |  Nro Descargas: %d
                """,b.getTitulo(), b.getBookId(),b.getCantidadDeDescargas()));
            System.out.println("\nSi encontraste tu libro usa el id y realiza una busqueda por id\n");
        }else{
            System.out.println("Sorry bro!, no hubo coincidencias");
        }


    }
    @Transactional
    public void busquedaExactaPorID() {
        System.out.println("Escribe el id del libro que deseas buscar");
        var mybookID = keyboard.nextInt();
        keyboard.nextLine();
        var json = myRequest.getDatafromAPI(URL_BASE+"?ids="+mybookID);

        GutendexResponse libroResponse = converter.getData(json, GutendexResponse.class);
        if (libroResponse == null || libroResponse.results() == null || libroResponse.results().isEmpty()) {
            System.out.println("No se encontraron coincidencias");
            return;
        }
        
        // Obtener los datos del libro de la API
        GutendexResults libroData = libroResponse.results().get(0);
        
        // Verificar si el libro ya existe
        Optional<Libro> libroExistente = repositorio.findByBookId(libroData.bookId());
        if (libroExistente.isPresent()) {
            System.out.println("El libro ya está guardado en la base de datos");
            return;
        }
        
        // Mostrar información del libro
        System.out.println("*******  Book  *******");
        System.out.println("Title: " + libroData.titulo());
        System.out.println("Id: " + libroData.bookId());
        System.out.println("Author: " + (libroData.autores() != null && !libroData.autores().isEmpty() ? 
            String.join(", ", libroData.autores().stream().map(a -> a.nombreDelAutor()).toList()) : "Desconocido"));
        System.out.println("Language: " + String.join(", ", libroData.idiomas()));
        System.out.println("Copyright: " + libroData.derechosDeAutor());
        System.out.println("Media Type: " + libroData.tipoMedia());
        System.out.println("Downloads: " + libroData.cantidadDeDescargas());
        System.out.println("***********************\n");

        String mensaje = """
                Desea Guardar el libro en la base de datos?
                1.- Yes
                2.- No
                """;
        System.out.println(mensaje);
        var answer = keyboard.nextInt();
        keyboard.nextLine();
        
        if (answer == 1) {
            try {
                // Crear una nueva instancia de Libro
                Libro libroAGuardar = new Libro();
                libroAGuardar.setBookId(libroData.bookId());
                libroAGuardar.setTitulo(libroData.titulo());
                libroAGuardar.setIdioms(String.join("-", libroData.idiomas()));
                libroAGuardar.setDerechosDeAutor(libroData.derechosDeAutor());
                libroAGuardar.setTipoMedia(libroData.tipoMedia());
                libroAGuardar.setCantidadDeDescargas(libroData.cantidadDeDescargas());
                
                // Guardar el libro primero para obtener un ID
                libroAGuardar = repositorio.save(libroAGuardar);
                
                // Procesar cada autor del libro
                if (libroData.autores() != null && !libroData.autores().isEmpty()) {
                    for (GutendexAutor autorData : libroData.autores()) {
                        // Buscar autor existente por nombre
                        Optional<Autor> autorExistente = autorRepositorio.findByNombreDelAutor(autorData.nombreDelAutor());
                        
                        if (autorExistente.isPresent()) {
                            // Usar el autor existente
                            Autor autorBD = autorExistente.get();
                            // Verificar si ya está asociado al libro usando el ID
                            boolean yaAsociado = false;
                            for (Libro libroDelAutor : autorBD.getLibros()) {
                                if (libroDelAutor.getId() != null && 
                                    libroDelAutor.getId().equals(libroAGuardar.getId())) {
                                    yaAsociado = true;
                                    break;
                                }
                            }
                            if (!yaAsociado) {
                                // Usar el método agregarAutor que maneja la relación bidireccional
                                autorBD.getLibro().add(libroAGuardar);
                                libroAGuardar.getAutor().add(autorBD);
                            }
                        } else {
                            // Crear un nuevo autor
                            Autor nuevoAutor = new Autor(
                                autorData.nombreDelAutor(),
                                autorData.nacimientoDelAutor(),
                                autorData.fallecimientoDelAutor()
                            );
                            // Inicializar la lista de libros del autor
                            nuevoAutor.setLibro(new ArrayList<>());
                            // Guardar el autor primero
                            nuevoAutor = autorRepositorio.save(nuevoAutor);
                            // Luego agregarlo al libro
                            libroAGuardar.agregarAutor(nuevoAutor);
                        }
                    }
                    // Guardar los cambios en el libro
                    repositorio.save(libroAGuardar);
                }
                
                System.out.println("Libro guardado con éxito");
            } catch (Exception e) {
                System.err.println("Error al guardar el libro: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void mostrarLibrosGuardados() {
        List<Libro> libros = repositorio.findAll();
        libros.forEach(System.out::println);
    }
    private void top5MasDescargados(){
        List<Libro> top5 = repositorio.findTop5ByOrderByCantidadDeDescargasDesc();
        top5.forEach(l -> System.out.println("Libro: " + l.getTitulo() + ". | Downloads: " + l.getCantidadDeDescargas()));
    }
    private void listarAutoresRegistrados(){
        System.out.println("Ingrese el nombre del autor que desea buscar use este formato apellido, nombre");
        var nombreDelAutor = keyboard.nextLine();
        Optional<Autor> autorOptional = autorRepositorio.findByNombreDelAutor(nombreDelAutor);
        if (autorOptional.isPresent()){
            Autor autor = autorOptional.get();
            System.out.println(autor);
            System.out.println("---------------------");
        }else{
            System.out.println("Sorry bro, tu autor de libros favorito no esta en la base de datos");
        }
    }
    private void listarLibrosPorIdioma(){
        System.out.println("Ingrese el idioma que desea buscar");
        var idioma = keyboard.nextLine();
        List<Libro> librosPorIdioma = repositorio.findByIdioms(idioma);
        librosPorIdioma.forEach(System.out::println);
    }
}
//        case 1:
//        if (!repositorio.existsById(myBook.getBookId())) {
//            List<Autor> autoresProcesados = new ArrayList<>();
//            for (Autor autor : myBook.getAutor()){
//                Optional<Autor> autorExistente = autorRepositorio.findByNombreDelAutor(autor.getNombreDelAutor());
//                if (autorExistente.isPresent()){
//                    autoresProcesados.add(autorExistente.get());
//                }else{
//                    Autor nuevoAutor = autorRepositorio.save(autor);
//                    autoresProcesados.add(nuevoAutor);
//                }
//            }
//            myBook.setAutor(autoresProcesados);
//            Libro libroGuardado = repositorio.save(myBook);
//            for (Autor autor : libroGuardado.getAutor()){
//                if (!autor.getLibros().contains(libroGuardado)){
//                    autor.getLibros().add(libroGuardado);
//                    autorRepositorio.save(autor);
//                }
//            }
//            System.out.println("Libro guardado exitosamente");
//        } else {
//            System.out.println("EL libro ya existe en la base de datos");
//        }
//        break;
//        case 2:
//        break;
//        default:
//        System.out.println("Entrada Invalida");