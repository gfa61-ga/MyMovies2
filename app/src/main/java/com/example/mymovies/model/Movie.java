package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private final String originalTitle;
    private final String posterPath;
    private final String backdropPath;
    private final String overview;
    private final String voteAverage;
    private final String releaseDate;

    public Movie(String originalTitle, String posterPath, String backdropPath,
                 String overview, String voteAverage, String releaseDate) {
        String baseURL = "https://image.tmdb.org/t/p/w185";
        this.posterPath = baseURL + posterPath;

        this.originalTitle = originalTitle;
        this.backdropPath = baseURL + backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
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
        backdropPath = source.readString();
        overview = source.readString();
        voteAverage = source.readString();
        releaseDate = source.readString();
    }
}
