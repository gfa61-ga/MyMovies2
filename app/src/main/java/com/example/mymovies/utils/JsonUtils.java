package com.example.mymovies.utils;

import com.example.mymovies.model.Movie;
import com.example.mymovies.model.Review;
import com.example.mymovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static List<Movie> parseMovies(String apiResponse) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject moviesJson = new JSONObject(apiResponse);
            JSONArray results = moviesJson.getJSONArray("results");

            for (int index = 0; index < results.length(); index++) {
                JSONObject movieJson = results.getJSONObject(index);
                Movie movie = parseMovieFromJsonObject(movieJson);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static Movie parseMovieFromJsonObject(JSONObject movieJson) {
        String originalTitle = "";
        String posterPath = "";
        String backdropPath = "";
        String overview = "";
        String voteAverage = "";
        String releaseDate = "";
        String movieId = "";

        try {
            originalTitle = movieJson.getString("original_title");
            posterPath = movieJson.getString("poster_path");
            backdropPath = movieJson.getString("backdrop_path");
            overview = movieJson.getString("overview");
            voteAverage = movieJson.getString("vote_average");
            releaseDate = movieJson.getString("release_date");
            movieId = movieJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Movie(originalTitle, posterPath, backdropPath, overview, voteAverage, releaseDate, movieId);
    }

    public static List<Trailer> parseTrailers(String apiResponse) {
        List<Trailer> trailers = new ArrayList<>();
        try {
            JSONObject trailersJson = new JSONObject(apiResponse);
            JSONArray results = trailersJson.getJSONArray("results");

            for (int index = 0; index < results.length(); index++) {
                JSONObject trailerJson = results.getJSONObject(index);
                Trailer trailer = parseTrailerFromJsonObject(trailerJson);
                if (trailer.getType().equals("Trailer")) { // Get only "trailer" videos
                    trailers.add(trailer);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public static Trailer parseTrailerFromJsonObject(JSONObject trailerJson) {
        String key = "";
        String name = "";
        String type = "";

        try {
            key = trailerJson.getString("key");
            name = trailerJson.getString("name");
            type = trailerJson.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Trailer(key, name, type);
    }

    public static List<Review> parseReviews(String apiResponse) {
        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject reviewsJson = new JSONObject(apiResponse);
            JSONArray results = reviewsJson.getJSONArray("results");

            for (int index = 0; index < results.length(); index++) {
                JSONObject reviewJson = results.getJSONObject(index);
                Review review = parseReviewFromJsonObject(reviewJson);
                try {
                    if (Float.parseFloat(review.getRating()) > 5) { // Get only reviews with rating > 5
                        reviews.add(review);
                    }
                } catch (NumberFormatException e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public static Review parseReviewFromJsonObject(JSONObject reviewJson) {
        String author = "";
        String rating = "";
        String content = "";

        try {
            author = reviewJson.getString("author");
            JSONObject authorDetails = reviewJson.getJSONObject("author_details");
            rating = authorDetails.getString("rating");
            content = reviewJson.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Review(author, rating, content);
    }
}
