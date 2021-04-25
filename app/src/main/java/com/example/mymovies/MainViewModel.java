package com.example.mymovies;
import androidx.lifecycle.AndroidViewModel;

import com.example.mymovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/*
 Create MainViewModel to store mApiResponseJson array.
 This way we avoid repeating the same API calls while the app is running.
*/
public class MainViewModel extends AndroidViewModel  {
    private ArrayList<String>[] mApiResponseJson;
    private List<Movie> moviesList = new ArrayList<>();
    String[] mTrailersAndReviewsApiResponseJson;

    // This constructor creates a "MainViewModel has no zero argument constructor" error ???????
    /*
    public MainViewModel(@NonNull Application application) {
        super(application);
   */
    public MainViewModel() {
        super(null); // This only works with a null argument ???????

        mApiResponseJson=new ArrayList[2];

        // Create ArrayList to store API responses for most popular_movies pages and reuse them, if needed while tha app is open
        int mostPopularIndex=0;
        mApiResponseJson[mostPopularIndex] = new ArrayList<String>();

        // Create ArrayList to store API responses for top rated_movies pages and reuse them, if needed while tha app is open
        int topRatedIndex=1;
        mApiResponseJson[topRatedIndex] = new ArrayList<String>();

        // Create String array to store API response for movie's trailers and reviews, in order to restore them after rotation
        mTrailersAndReviewsApiResponseJson = new String[2];
    }

    public ArrayList<String>[] getmApiResponceJson() {
        return mApiResponseJson;
    }

    public List<Movie> getMoviesFromModel() {
        return moviesList;
    }

    public void setMoviesOfModel(List<Movie> currentMoviesList) {
        moviesList = new ArrayList<Movie>();
        if (currentMoviesList!= null) {
            moviesList.addAll(currentMoviesList);
        }
    }

    public String[] getTrailersAndReviewsApiResponseJson() {
        return mTrailersAndReviewsApiResponseJson;
    }
}