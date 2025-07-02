package com.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexAutor(
        @JsonAlias("name") String nombreDelAutor,
        @JsonAlias("birth_year") Integer nacimientoDelAutor,
        @JsonAlias("death_year") Integer fallecimientoDelAutor
) {
}
