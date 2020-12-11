package com.example.mymovies.utils;

import com.example.mymovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Movie parseMovieJson(JSONObject movieJson) {
        String originalTitle = "";
        String posterPath = "";
        String overview = "";
        String voteAverage = "";
        String releaseDate = "";

        try {
            originalTitle = movieJson.getString("original_title");
            posterPath = movieJson.getString("poster_path");
            overview = movieJson.getString("overview");
            voteAverage = movieJson.getString("vote_average");
            releaseDate = movieJson.getString("release_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate);
    }

    public static List<Movie> parseMovies(String apiResponse) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject moviesJson = new JSONObject(apiResponse);
            JSONArray results = moviesJson.getJSONArray("results");

            for (int index = 0; index < results.length(); index++) {
                JSONObject movieJson = results.getJSONObject(index);
                Movie movie = parseMovieJson(movieJson);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
