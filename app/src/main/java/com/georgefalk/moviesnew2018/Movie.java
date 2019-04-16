package com.georgefalk.moviesnew2018;

public class Movie {

    private String id;
    private String name;
    private String body;
    private String url;
    private String year;



    public Movie(String name, String body, String url, String year) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.url = url;
        this.year = year;
    }

    public Movie(String id, String name, String body, String url, String year) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.url = url;
        this.year = year;
    }


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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
