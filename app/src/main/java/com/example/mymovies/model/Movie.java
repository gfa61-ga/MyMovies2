package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/** Implements Parcelable so that movies can be serialized
 * and passed through savedInstanceState bundles and through Intent extras
 */
@Entity(tableName = "movies")
public class Movie implements Parcelable {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "movie_id")
    private String movieId;
    @ColumnInfo(name = "original_title")
    private String originalTitle;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;
    private String overview;
    @ColumnInfo(name = "vote_average")
    private String voteAverage;
    @ColumnInfo(name = "release_date")
    private String releaseDate;

    public Movie(String originalTitle, String posterPath, String backdropPath,
                 String overview, String voteAverage, String releaseDate, @NonNull String movieId) {

        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.movieId = movieId;
    }

    @NonNull
    public String getMovieId() { return movieId; }

    public String getOriginalTitle() { return originalTitle; }

    public String getPosterPath() { return posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public String getOverview() { return overview; }

    public String getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }

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
