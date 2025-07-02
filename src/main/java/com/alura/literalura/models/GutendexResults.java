package com.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexResults(
        @JsonAlias("id") Integer bookId,
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<GutendexAutor> autores,
        @JsonAlias("summaries") List<String> resumenes,
        @JsonAlias("translators") List<GutendexTraductor> traductores,
        @JsonAlias("subjects") List<String> temas,
        @JsonAlias("bookshelves") List<String> estanterias,
        @JsonAlias("languages") List<String> idiomas,
        @JsonAlias("copyright") boolean derechosDeAutor,
        @JsonAlias("media_type") String tipoMedia,
        @JsonAlias("formats") Map<String, String> formatos,
        @JsonAlias("download_count") Integer cantidadDeDescargas
) {

}
