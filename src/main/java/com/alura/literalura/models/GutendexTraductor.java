package com.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexTraductor(
        @JsonAlias("name") String nombreDelTraductor,
        @JsonAlias("birth_year") Integer nacimientoDelTraductor,
        @JsonAlias("death_year") Integer fallecimientoDelTraductor
) {
}
