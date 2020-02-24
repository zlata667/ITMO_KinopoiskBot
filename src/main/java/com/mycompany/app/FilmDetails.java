package com.mycompany.app;

import java.util.List;
import java.util.Map;

class FilmDetails {

    private int id;
    private int trailerId;
    private String relativeUrl;
    private String title;
    private String originalTitle;
    private List<String> genres;
    private String firstGenre;
    private Map<String, Object> ratings;
    private List<String> countries;
    private String year;
    private Map<String,String> contextData;
    private String posterBaseUrl;

    public String getPosterBaseUrl() {
        return posterBaseUrl;
    }

    public void setPosterBaseUrl(String posterBaseUrl) {
        this.posterBaseUrl = posterBaseUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(int trailerId) {
        this.trailerId = trailerId;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Map<String, String> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, String> contextData) {
        this.contextData = contextData;
    }

    public String getFirstGenre() {
        return firstGenre;
    }

    public void setFirstGenre(String firstGenre) {
        this.firstGenre = firstGenre;
    }

    public Map<String, Object> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Object> ratings) {
        this.ratings = ratings;
    }
}