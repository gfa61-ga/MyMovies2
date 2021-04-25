package com.example.mymovies.model;

public class Review {
    private final String author;
    private final String rating;
    private final String content;

    public Review(String author, String rating, String content) {

        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    public String getAuthor() { return author; }

    public String getRating() { return rating; }

    public String getContent() { return content; }
}
