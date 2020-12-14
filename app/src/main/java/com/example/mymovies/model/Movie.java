package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    private final String baseURL = "https://image.tmdb.org/t/p/w185";

    public Movie(String originalTitle, String posterPath, String overview, String voteAverage, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterPath = baseURL + posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = baseURL + posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) { this.voteAverage = voteAverage; }

    public String getDate() {
        return releaseDate;
    }

    public void setDate(String releaseDate) { this.releaseDate = releaseDate; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
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

    private Movie(Parcel source) {
        originalTitle = source.readString();
        posterPath = source.readString();
        overview = source.readString();
        voteAverage = source.readString();
        releaseDate = source.readString();
    }
}
