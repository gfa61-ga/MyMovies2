package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;
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

    public String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";
    /* Constructor that creates new movie from movieData */
    public Movie(String originalTitle, String posterPath, String backdropPath,
                 String overview, String voteAverage, String releaseDate, String movieId) {
        // w185 is the path for getting an image with 185 dpi width


        this.posterPath = IMAGES_BASE_URL + posterPath;
        this.originalTitle = originalTitle;
        this.backdropPath = IMAGES_BASE_URL + backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.movieId = movieId;
    }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) {
        this.posterPath = IMAGES_BASE_URL +  posterPath;
    }

    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String backdropPath) {
        this.backdropPath =IMAGES_BASE_URL + backdropPath;
    }

    public String getOverview() { return overview; }
    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() { return voteAverage; }
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

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
