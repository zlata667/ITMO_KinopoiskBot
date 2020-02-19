package com.mycompany.app;

import java.util.Map;

public class FilmOrPerson {

    private String id;
    private String name;
    private String title;
    private String url;
    private String originalName;
    private String originalTitle;
    private String year;
    private String birthYear;
    private String yearsRange;
    private Map<String, String> imgSrcSet;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getYear() {
        return year;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getYearsRange() {
        return yearsRange;
    }

    public void setYearsRange(String yearsRange) {
        this.yearsRange = yearsRange;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Map<String, String> getImgSrcSet() {
        return imgSrcSet;
    }

    public void setImgSrcSet(Map<String, String> imgSrcSet) {
        this.imgSrcSet = imgSrcSet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
