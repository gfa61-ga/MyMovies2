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
    private List<Movie> moviesList = new ArrayList<Movie>();
    String[] mTrailersApiResponseJson;

    // This constructor creates a "MainViewModel has no zero argument constructor" error
    /*
    public MainViewModel(@NonNull Application application) {
        super(application);
   */
    public MainViewModel() {
        super(null); // This only works with a null argument ???????

        mApiResponseJson=new ArrayList[2];

        mTrailersApiResponseJson = new String[2];

        // Create ArrayList to store API responses for most popular movies
        int mostPopularIndex=0;
        mApiResponseJson[mostPopularIndex] = new ArrayList<String>();

        // Create ArrayList to store API responses for top rated movies
        int topRatedIndex=1;
        mApiResponseJson[topRatedIndex] = new ArrayList<String>();
    }

    public ArrayList<String>[] getmApiResponceJson() {
        return mApiResponseJson;
    }

    public String[] getTrailersApiResponseJson() {
        return mTrailersApiResponseJson;
    }

    public List<Movie> getMoviesFromModel() {
        return moviesList;
    }

    public void setMoviesFromModel(List<Movie> currentMoviesList) {
        moviesList = new ArrayList<Movie>();
        if (currentMoviesList!= null) {
            moviesList.addAll(currentMoviesList);
        }
    }
}