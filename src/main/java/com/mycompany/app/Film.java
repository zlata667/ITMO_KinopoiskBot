package com.mycompany.app;

import java.util.List;

class Film{
    private List<FilmDetails> filmography;

    List<FilmDetails> getFilmography() {
        return filmography;
    }

    public void setFilmography(List<FilmDetails> filmography) {
        this.filmography = filmography;
    }
}