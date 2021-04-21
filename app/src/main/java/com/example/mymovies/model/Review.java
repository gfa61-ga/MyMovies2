package com.example.mymovies.model;

/** Implements Parcelable so that movies can be serialized
 * and passed through savedInstanceState bundles and through Intent extras
 */
public class Review {
    private final String author;
    private final String rating;
    private final String content;

    /* Constructor that creates new movie from movieData */
    public Review(String author, String rating, String content) {

        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    public String getAuthor() { return author; }

    public String getRating() { return rating; }

    public String getContent() { return content; }

}
