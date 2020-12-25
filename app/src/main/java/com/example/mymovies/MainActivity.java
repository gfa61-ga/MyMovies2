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

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickHandler{

    private RecyclerView moviesDisplayRecyclerView;
    private MovieAdapter moviesDisplayAdapter;
    private TextView errorDisplayTextView;
    private ProgressBar mProgressBar;
    private GridLayoutManager mGridLayoutManager;
    private Toast mToast;
    private Context context;
    private MovieAdapter.OnClickHandler mClickHandler;
    private String sortByPath;
    private int apiPage;
    private boolean internetConnection;
    private boolean MoviesDisplayScrolledDown;

    private final String SORT_BY_POPULAR_API_PATH = "popular";
    private final String SORT_BY_TOP_RATED_API_PATH = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorDisplayTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesDisplayRecyclerView = findViewById(R.id.recyclerview_movies);

        final int COLUMN_WIDTH_DP  = 250;
        int numOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), COLUMN_WIDTH_DP);

        context = this;
        mGridLayoutManager = new GridLayoutManager(context, numOfColumns);
        mGridLayoutManager.setReverseLayout(false);
        mGridLayoutManager.canScrollVertically();
        moviesDisplayRecyclerView.setLayoutManager(mGridLayoutManager);
        moviesDisplayRecyclerView.setHasFixedSize(true);

        mClickHandler = this;
        moviesDisplayAdapter = new MovieAdapter(mClickHandler, mGridLayoutManager);
        moviesDisplayRecyclerView.setAdapter(moviesDisplayAdapter);

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey("sortByPath"))
                sortByPath = savedInstanceState.getString("sortByPath");
            if (savedInstanceState.containsKey("internetConnection"))
                internetConnection = savedInstanceState.getBoolean("internetConnection");
            if (savedInstanceState.containsKey("apiPage"))
                apiPage = savedInstanceState.getInt("apiPage");
            if (savedInstanceState.containsKey("movies")) {
                moviesDisplayAdapter.setMovies(savedInstanceState.getParcelableArrayList("movies"));
                moviesDisplayAdapter.notifyDataSetChanged();
            }
        } else {
            sortByPath = SORT_BY_POPULAR_API_PATH;
            apiPage = 0;
            loadNextPageOfMovies();
        }

        moviesDisplayRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && MoviesDisplayScrolledDown ) {
                    apiPage++;
                    MoviesDisplayScrolledDown = false;

                    loadNextPageOfMovies();
                }
            }

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    MoviesDisplayScrolledDown = true;
                } else if (dy < 0) {
                    MoviesDisplayScrolledDown = false;
                }
            }
        });

        if (!internetConnection && moviesDisplayAdapter.getMovies() == null) {
            showErrorMessage();
        } else {
            showJsonDataView();
        }
    }

    private void showJsonDataView() {
        moviesDisplayRecyclerView.setVisibility(View.VISIBLE);
        errorDisplayTextView.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        moviesDisplayRecyclerView.setVisibility(View.GONE);
        errorDisplayTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (sortByPath.equals(SORT_BY_TOP_RATED_API_PATH)) {
            menu.findItem(R.id.action_order_top_rated).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.setChecked(true);
        int id = item.getItemId();
        
        String selectedSortByPath = SORT_BY_POPULAR_API_PATH;
        if (id == R.id.action_order_top_rated) {
            selectedSortByPath = SORT_BY_TOP_RATED_API_PATH;
        }

        if (!selectedSortByPath.equals(sortByPath) || moviesDisplayAdapter.getMovies() == null) {
            sortByPath = selectedSortByPath;
            apiPage=0;
            moviesDisplayAdapter = new MovieAdapter(mClickHandler, mGridLayoutManager);
            moviesDisplayRecyclerView.setAdapter(moviesDisplayAdapter);
            loadNextPageOfMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sortByPath", sortByPath);
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

    @SuppressLint("StaticFieldLeak")
    public class ThemoviedbQueryTask extends AsyncTask <String, Void, String>{
        @Override
        protected void onPreExecute() {
            showJsonDataView();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String selectedSortByPath = params[0];
            String apiPageToQuery = params[1];

            URL url = NetworkUtils.buildUrl(selectedSortByPath, apiPageToQuery);
            try {
                return NetworkUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String apiResponse) {
            mProgressBar.setVisibility(View.GONE);
            if (apiResponse != null) {
                apiPage++;
                internetConnection = true;
                showJsonDataView();
                moviesDisplayAdapter.loadMoreMovies(apiResponse);
            } else {
                if (apiPage>=1) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    Context toastContext = MainActivity.this;
                    String errorMessage = "No internet connection.\\nPlease try again later.";
                    mToast = Toast.makeText(toastContext, errorMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    mToast.show();
                } else {
                    showErrorMessage();
                    internetConnection = false;
                }
            }
        }
    }

    private void loadNextPageOfMovies() {
        String apiPageToQuery = String.valueOf(apiPage+1);
        new ThemoviedbQueryTask().execute(sortByPath, apiPageToQuery);
    }
}