package com.example.mymovies;
import androidx.lifecycle.AndroidViewModel;
import java.util.ArrayList;

// Create MainViewModel to store mApiResponceJson array
public class MainViewModel extends AndroidViewModel  {
    public ArrayList<String>[] mApiResponseJson ;

    // This constructor creates a "MainViewModel has no zero argument constructor" error
    /*
    public MainViewModel(@NonNull Application application) {
        super(application);
   */
    public MainViewModel() {
        super(null); // This only works with a null argument ???????

        mApiResponseJson=new ArrayList[2];

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
}