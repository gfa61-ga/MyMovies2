package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.LayoutUtils;
import com.example.mymovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Defines the mainActivity instance, which also
 * handles clicks on moviePosters displayed in moviesDisplayRecyclerView
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickHandler{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorDisplayTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesDisplayRecyclerView = findViewById(R.id.recyclerview_movies);

        final int COLUMN_WIDTH_DP  = 185; // API posters have width equal to 185 dpi
        int numOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), COLUMN_WIDTH_DP);

        context = this;
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, numOfColumns);
        mGridLayoutManager.setReverseLayout(false);
        mGridLayoutManager.canScrollVertically();
        moviesDisplayRecyclerView.setLayoutManager(mGridLayoutManager);
        moviesDisplayRecyclerView.setHasFixedSize(true);

        MovieAdapter.OnClickHandler mClickHandler = this;
        moviesDisplayAdapter = new MovieAdapter(mClickHandler, mGridLayoutManager);
        moviesDisplayRecyclerView.setAdapter(moviesDisplayAdapter);

        if(savedInstanceState != null) { // Restores state after orientation change
            if (savedInstanceState.containsKey("sortByApiPath"))
                sortByApiPath = savedInstanceState.getString("sortByApiPath");
            if (savedInstanceState.containsKey("internetConnection"))
                internetConnection = savedInstanceState.getBoolean("internetConnection");
            if (savedInstanceState.containsKey("apiPage"))
                apiPage = savedInstanceState.getInt("apiPage");
            if (savedInstanceState.containsKey("movies")) {
                moviesDisplayAdapter.setMovies(savedInstanceState.getParcelableArrayList("movies"));
                moviesDisplayAdapter.notifyDataSetChanged();
            }
        } else { // Starts new state
            sortByApiPath = SORT_BY_POPULAR_API_PATH;
            // Considers internetConnection is true until the first try to get a page of movies
            internetConnection = true;
            apiPage = 0;
            loadNextPageOfMovies();
        }

        moviesDisplayRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int directionDown = 1; // direction integers: -1 for up, 1 for down
                // Loads more movies when we are at the last line of movies and try to scroll down farther
                if (!recyclerView.canScrollVertically(directionDown) && moviesDisplayScrolledDown ) {
                    moviesDisplayScrolledDown = false;
                    loadNextPageOfMovies();
                }
            }

            /** Updates vertical scroll state after vertical scrolling */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    moviesDisplayScrolledDown = true;
                } else if (dy < 0) {
                    moviesDisplayScrolledDown = false;
                }
            }
        });

        // Displays internet connection error when internet is off and no movies are previously loaded
        if (!internetConnection && moviesDisplayAdapter.getMovies() == null) {
            showInternetConnectionErrorMessage();
        }
    }

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

        // Resets movies state and tries to load next page  movies
        // when we select a different sortBy option OR
        // when we select the same sortBy option and no movies have previously loaded - works like "RETRY"
        if (!selectedSortByApiPath.equals(sortByApiPath) || moviesDisplayAdapter.getMovies() == null) {
            sortByApiPath = selectedSortByApiPath;
            apiPage=0;
            moviesDisplayAdapter.setMovies(null);
            moviesDisplayRecyclerView.scrollToPosition(0);
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
        outState.putParcelableArrayList("movies", (ArrayList<Movie>) moviesDisplayAdapter.getMovies());
        outState.putBoolean("internetConnection",internetConnection);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Movie movie) {
        launchMovieDetailsActivity(movie);
    }

    private void launchMovieDetailsActivity(Movie movie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    /** Defines an AsyncTask instance, which tries to load the next page of movies */
    @SuppressLint("StaticFieldLeak")
    public class ThemoviedbQueryTask extends AsyncTask <String, Void, String>{
        @Override
        protected void onPreExecute() {
            showMoviesDisplayRecyclerView();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /** Tries to asynchronously get an apiResponse as a String */
        @Override
        protected String doInBackground(String... params) {
            String sortByApiPath = params[0];
            String apiPageToQuery = params[1];

            URL url = NetworkUtils.buildUrl(sortByApiPath, apiPageToQuery);
            try {
                return NetworkUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /** Handles the apiResponse */
        @Override
        protected void onPostExecute(String apiResponse) {
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
                    TextView v = (TextView) noInternetToast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    noInternetToast.show();
                } else {
                    showInternetConnectionErrorMessage();
                }
            }
        }
    }

    /** Creates and executes the AsyncTask instance */
    private void loadNextPageOfMovies() {
        String apiPageToQuery = String.valueOf(apiPage+1);
        new ThemoviedbQueryTask().execute(sortByApiPath, apiPageToQuery);
    }
}