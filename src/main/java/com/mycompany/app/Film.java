package com.mycompany.app;

import java.util.List;

class Film{
    private List<FilmDetails> filmography;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<FilmDetails> getFilmography() {
        return filmography;
    }

    public void setFilmography(List<FilmDetails> filmography) {
        this.filmography = filmography;
    }
}