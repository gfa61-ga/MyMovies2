package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/** Implements Parcelable so that movies can be serialized
 * and passed through savedInstanceState bundles and through Intent extras
 */
public class Movie implements Parcelable {
    private final String originalTitle;
    private final String posterPath;
    private final String backdropPath;
    private final String overview;
    private final String voteAverage;
    private final String releaseDate;
    private final String movieId;

    /* Constructor that creates new movie from movieData */
    public Movie(String originalTitle, String posterPath, String backdropPath,
                 String overview, String voteAverage, String releaseDate, String movieId) {
        // w185 is the path for getting an image with 185 dpi width
        final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

        this.posterPath = IMAGES_BASE_URL + posterPath;
        this.originalTitle = originalTitle;
        this.backdropPath = IMAGES_BASE_URL + backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.movieId = movieId;
    }

    public String getOriginalTitle() { return originalTitle; }

    public String getPosterPath() { return posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public String getOverview() { return overview; }

    public String getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }

    public String getMovieId() { return movieId; }

    /* Constructor that creates new movie from parcelSource */
    private Movie(Parcel source) {
        originalTitle = source.readString();
        posterPath = source.readString();
        backdropPath = source.readString();
        overview = source.readString();
        voteAverage = source.readString();
        releaseDate = source.readString();
        movieId = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(movieId);
    }

    public static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
}
