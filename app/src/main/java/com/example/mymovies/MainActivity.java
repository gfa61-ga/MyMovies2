package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Loader;
import android.content.AsyncTaskLoader;
import android.app.LoaderManager;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.database.AppDatabase;
//import com.example.mymovies.database.MovieEntry;
import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.LayoutUtils;
import com.example.mymovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Defines the mainActivity instance, which also
 * handles clicks on moviePosters displayed in moviesDisplayRecyclerView
 * and contains the Loader callbacks
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickHandler,
        LoaderManager.LoaderCallbacks<String>{

    private final int NEXT_MOVIE_PAGE_DATA_LOADER = 31;
    private static final String SORT_BY_EXTRA = "sort-by";
    private static final String PAGE_NUMBER_EXTRA = "page-number";

    private RecyclerView moviesDisplayRecyclerView;
    private MovieAdapter moviesDisplayAdapter;
    private TextView errorDisplayTextView;
    private ProgressBar mProgressBar;
    private Context context;
    private String sortByApiPath;
    private int apiPage;
    private boolean internetConnection;
    private boolean moviesDisplayScrolledDown;

    // As defined at: https://developers.themoviedb.org/3/movies/get-popular-movies
    private final String SORT_BY_POPULAR_API_PATH = "popular";
    // As defined at: https://developers.themoviedb.org/3/movies/get-top-rated-movies
    private final String SORT_BY_TOP_RATED_API_PATH = "top_rated";

    //
    private final String SORT_BY_FAVORITES = "favorites";

    private ArrayList<String>[] mApiResponseJson ;
    private MainViewModel mMainActivityModel;
    private LoaderManager mLoaderManager;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize member variable for the data base
        db = AppDatabase.getInstance(getApplicationContext());

        mLoaderManager = getLoaderManager(); //we must call getLoaderManager()
        // in onCreate() if it's an Activity or in onActivityCreated() if it's a Fragment
        // Otherwise the loader will be in a STOPPED state AFTER ROTATION
        // because the loaderManager will not reattach the old loader to the new Activity
        // https://stackoverflow.com/questions/12507617/android-loader-not-triggering-callbacks-on-screen-rotate

        errorDisplayTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesDisplayRecyclerView = findViewById(R.id.recyclerview_movies);

        final int COLUMN_WIDTH_DP = 185; // API posters have width equal to 185 dpi
        int numOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), COLUMN_WIDTH_DP);

        context = this;
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, numOfColumns);
        mGridLayoutManager.setReverseLayout(false);
        mGridLayoutManager.canScrollVertically();
        moviesDisplayRecyclerView.setLayoutManager(mGridLayoutManager);
        moviesDisplayRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null) { // Restores state after orientation change
            if (savedInstanceState.containsKey("sortByApiPath"))
                sortByApiPath = savedInstanceState.getString("sortByApiPath");
        } else {
            sortByApiPath = SORT_BY_POPULAR_API_PATH;
        }

        MovieAdapter.OnClickHandler mClickHandler = this;
        moviesDisplayAdapter = new MovieAdapter(mClickHandler, mGridLayoutManager, sortByApiPath);
        moviesDisplayRecyclerView.setAdapter(moviesDisplayAdapter);

        // Get mApiResponceJson array from the mMainActivityModel, where it is automatically retained
        // during configuration changes like rotation.
        mMainActivityModel = new ViewModelProvider(this).get(MainViewModel.class);
        mApiResponseJson = mMainActivityModel.getmApiResponceJson();

        if(savedInstanceState != null) { // Restores state after orientation change
            if (savedInstanceState.containsKey("internetConnection"))
                internetConnection = savedInstanceState.getBoolean("internetConnection");
            if (savedInstanceState.containsKey("apiPage"))
                apiPage = savedInstanceState.getInt("apiPage");

            // get moviesList from mMainActivityModel after rotation
            moviesDisplayAdapter.setMovies(mMainActivityModel.getMoviesFromModel());
            // Adjusts the moviesDisplayRecyclerView, because the number of rows
            // may change after rotation, so that the last row of posters
            // if full. The number of rows after rotation
            // is calculated by the MovieAdapter.getItemCount() method
            moviesDisplayAdapter.notifyDataSetChanged();
        } else { // Starts new state
            // Considers internetConnection is true until the first try to get a page of movies
            internetConnection = true;
            apiPage = 0;
            loadNextPageOfMovies();
        }

        moviesDisplayRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /** Loads more movies when we are at the last line of movies and try to scroll down farther */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int directionDown = 1; // Direction integers: -1 for up, 1 for down
                if (!recyclerView.canScrollVertically(directionDown) && moviesDisplayScrolledDown ) {
                    moviesDisplayScrolledDown = false;
                    loadNextPageOfMovies();
                }
            }

            /** Updates vertical scroll state after vertical scrolling */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy >= 0) {
                    moviesDisplayScrolledDown = true;
                } else {
                    moviesDisplayScrolledDown = false;
                }
            }
        });

        // Displays internet connection error when internet is off and no movies are previously loaded
        if (!internetConnection && moviesDisplayAdapter.getMovies().size() == 0) {
            showInternetConnectionErrorMessage();
        }
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        if (sortByApiPath.equals(SORT_BY_FAVORITES)) {

            moviesDisplayAdapter.notifyDataSetChanged();

        }

    }*/

    private void showMoviesDisplayRecyclerView() {
        moviesDisplayRecyclerView.setVisibility(View.VISIBLE);
        errorDisplayTextView.setVisibility(View.GONE);
    }

    private void showInternetConnectionErrorMessage() {
        moviesDisplayRecyclerView.setVisibility(View.GONE);
        errorDisplayTextView.setVisibility(View.VISIBLE);
    }

    /** adds items to the action bar, if it is present. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // R.id.action_order_most_popular item is checked by default, as defined in menu_main.xml
        if (sortByApiPath.equals(SORT_BY_TOP_RATED_API_PATH)) {
            menu.findItem(R.id.action_order_top_rated).setChecked(true);
        }
        if (sortByApiPath.equals(SORT_BY_FAVORITES)) {
            menu.findItem(R.id.action_order_favorites).setChecked(true);
        }

        return true;
    }

    /** Handles action bar item clicks. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        // Selects SORT_BY_POPULAR_API_PATH as default
        String selectedSortByApiPath = SORT_BY_POPULAR_API_PATH;
        if (id == R.id.action_order_top_rated) {
            selectedSortByApiPath = SORT_BY_TOP_RATED_API_PATH;
        }
        if (id == R.id.action_order_favorites) {
            selectedSortByApiPath = SORT_BY_FAVORITES;
            showMoviesDisplayRecyclerView();
        }

        // Resets movies state and tries to load next page  movies
        // when we select a different sortBy option OR
        // when we select the same sortBy option and no movies have previously loaded - works like "RETRY"
        if (!selectedSortByApiPath.equals(sortByApiPath) || moviesDisplayAdapter.getMovies() == null) {
            sortByApiPath = selectedSortByApiPath;
            moviesDisplayAdapter.setSortByApiPath(sortByApiPath);
            apiPage=0;
            moviesDisplayAdapter.setMovies(null);
            moviesDisplayRecyclerView.scrollToPosition(0); // Scrolls to the top
            loadNextPageOfMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Saves state before orientation change */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sortByApiPath", sortByApiPath);
        outState.putInt("apiPage", apiPage);
        outState.putBoolean("internetConnection",internetConnection);
        // stores moviesList to mMainActivityModel before rotation
        mMainActivityModel.setMoviesFromModel(moviesDisplayAdapter.getMovies());
        super.onSaveInstanceState(outState);
    }

    /** Handles MovieViewHolders moviePosterImageView clicks. */
    @Override
    public void onClick(Movie movie) {
        launchMovieDetailsActivity(movie);
    }

    private void launchMovieDetailsActivity(Movie movie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this){

            @Override
            protected void onStartLoading() {
                assert args != null;
                String sortByApiPath = args.getString(SORT_BY_EXTRA);
                String apiPageToQuery = args.getString(PAGE_NUMBER_EXTRA);
                int index;
                if (sortByApiPath.equals(SORT_BY_POPULAR_API_PATH)) {
                    index = 0;
                } else {
                    index=1;
                }

                if(mApiResponseJson[index].size()<Integer.parseInt(apiPageToQuery) ){
                    showMoviesDisplayRecyclerView();
                    if (internetConnection && !sortByApiPath.equals(SORT_BY_FAVORITES)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    forceLoad(); // This will ignore a previously loaded data set and load a new one.
                    // We generally should only call this when the loader is started.
                } else {
                    deliverResult(mApiResponseJson[index].get(Integer.parseInt(apiPageToQuery)-1));
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                if (args != null) {
                    String sortByApiPath = args.getString(SORT_BY_EXTRA);
                    String apiPageToQuery = args.getString(PAGE_NUMBER_EXTRA);

                    URL url = NetworkUtils.buildUrl(sortByApiPath, apiPageToQuery);
                    try {
                        return NetworkUtils.getResponseFromHttpUrl(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                return null;
            }

            @Override
            public void deliverResult(String apiResponse) {
                if (apiResponse != null) { // if apiResponse is null, DON'T save it in mApiResponceJson
                    String sortByApiPath = args.getString(SORT_BY_EXTRA);
                    String apiPageToQuery = args.getString(PAGE_NUMBER_EXTRA);
                    int index;
                    if (sortByApiPath.equals(SORT_BY_POPULAR_API_PATH)) {
                        index = 0; // Save apiResponse in most popular movies ArrayList
                    } else {
                        index = 1; // Save apiResponse in top rated movies ArrayList
                    }

                    if (mApiResponseJson[index].size() < Integer.parseInt(apiPageToQuery)) {
                        mApiResponseJson[index].add(Integer.parseInt(apiPageToQuery) - 1, apiResponse);
                    } else {
                        mApiResponseJson[index].set(Integer.parseInt(apiPageToQuery) - 1, apiResponse);
                    }
                }
                super.deliverResult(apiResponse);
            }
        };
    }

    @Override
    public void onLoadFinished(android.content.Loader<String> loader, String apiResponse) {
        mProgressBar.setVisibility(View.GONE);
        if (apiResponse != null) {
            apiPage++;
            internetConnection = true;
            showMoviesDisplayRecyclerView();
            moviesDisplayAdapter.loadMoreMovies(apiResponse);
        } else {
            internetConnection = false;
            if (apiPage>=1) { // If some movies have previously loaded don't hide them. Just show a toast
                Context toastContext = MainActivity.this;
                String errorMessage = "No internet connection.\nPlease try again later.";
                Toast noInternetToast = Toast.makeText(toastContext, errorMessage, Toast.LENGTH_LONG);

                // https://stackoverflow.com/questions/3522023/center-text-in-a-toast-in-android
                if (noInternetToast.getView() != null ) {
                    TextView v = (TextView) noInternetToast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                }

                noInternetToast.show();
            } else {
                showInternetConnectionErrorMessage();
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<String> loader) {
    }

    /** Creates or restarts and then asynchronously executes the anonymous AsyncTaskLoader instance */
    private void loadNextPageOfMovies() {
        if (!sortByApiPath.equals(SORT_BY_FAVORITES)) {
            String apiPageToQuery = String.valueOf(apiPage + 1);
            Bundle queryBundle = new Bundle();
            queryBundle.putString(SORT_BY_EXTRA, sortByApiPath);
            queryBundle.putString(PAGE_NUMBER_EXTRA, apiPageToQuery);

            Loader<String> mLoader = mLoaderManager.getLoader(NEXT_MOVIE_PAGE_DATA_LOADER);

            if (mLoader == null) {
                mLoaderManager.initLoader(NEXT_MOVIE_PAGE_DATA_LOADER, queryBundle, this);
            } else {
                // This could not restart the loader after rotation..!!!
                // getLoaderManager().restartLoader(NEXT_MOVIE_PAGE_DATA_LOADER, queryBundle, this);

                mLoaderManager.restartLoader(NEXT_MOVIE_PAGE_DATA_LOADER, queryBundle, this);
            }
        } else {

            final LiveData<List<Movie>> movies = db.movieDao().loadAllMovies();

            movies.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> updatedMovies) {
                    if (sortByApiPath.equals(SORT_BY_FAVORITES)) {
                        moviesDisplayAdapter.setMovies(updatedMovies);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    moviesDisplayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });

        }
    }
}