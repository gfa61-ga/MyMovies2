package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int selectedMovieCategoryId;
    private int page;
    private Toast mToast;
    private TextView mErrorDisplayTextView;
    private TextView mMoviesDisplayTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mErrorDisplayTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(savedInstanceState == null) {
            Log.w("MainActivity new start", "*********************************************");
        }
        if(savedInstanceState == null || !savedInstanceState.containsKey("selectedMovieCategoryId")) {
            selectedMovieCategoryId = R.string.most_popular_movies_json;
        }
        else {
            selectedMovieCategoryId = savedInstanceState.getInt("selectedMovieCategoryId");
        }
        if(savedInstanceState == null || !savedInstanceState.containsKey("page")) {
            page=1;
        }
        else {
            page = savedInstanceState.getInt("page");
        }
        mRecyclerView = findViewById(R.id.recyclerview_movies);

        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 185);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        mGridLayoutManager.setReverseLayout(false);
        mGridLayoutManager.canScrollVertically();
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            loadMovies();
        }
        else {
            mMovieAdapter.setMovies(savedInstanceState.getParcelableArrayList("movies"));
        }

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    //Toast.makeText(MainActivity.this, "Last", Toast.LENGTH_LONG).show();
                    page++;
                    //mMovieAdapter.readNextPage(page);
                    loadMovies();
                }
            }
        });
    }

    private void showJsonDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorDisplayTextView.setVisibility(View.GONE);

    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorDisplayTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.setChecked(true);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order_most_popular && selectedMovieCategoryId != R.string.most_popular_movies_json) {
            selectedMovieCategoryId = R.string.most_popular_movies_json;
            page=1;
            mMovieAdapter = new MovieAdapter(this);
            mRecyclerView.setAdapter(mMovieAdapter);
            loadMovies();
            return true;
        }
        if (id == R.id.action_order_top_rated && selectedMovieCategoryId != R.string.tor_rated_movies_json) {
            selectedMovieCategoryId = R.string.tor_rated_movies_json;
            page=1;
            mMovieAdapter = new MovieAdapter(this);
            mRecyclerView.setAdapter(mMovieAdapter);
            loadMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies() {
        // we temporary saved APIresponses to res/layout/strings.xml strings
        // to save an APIresponse to a string we must
        //    1. replace all  "  in the json object with  \"
        //    2. enclose the APIresponce json object between ""
        //String apiResponse = getResources().getString(selectedMovieCategoryId);

        new ThemoviedbQueryTask().execute(selectedMovieCategoryId, page);

        /*
        if (mMovieAdapter != null) {
            mMovieAdapter.loadMovies(apiResponse);
        }
         */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectedMovieCategoryId", selectedMovieCategoryId);
        outState.putInt("page", page);
        outState.putParcelableArrayList("movies", (ArrayList<Movie>) mMovieAdapter.getMovies());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        //mToast.show();
        launchMovieDetailsActivity(Integer.parseInt(message ));
    }
    private void launchMovieDetailsActivity(int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_INDEX, position);
        intent.putExtra("movie", mMovieAdapter.getMovies().get(position));
        startActivity(intent);
    }
    public class ThemoviedbQueryTask extends AsyncTask <Integer, Void, String>{
        @Override
        protected void onPreExecute() {
            showJsonDataView();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            int currentSelectedMovieCategoryId = params[0];
            int currentPage = params[1];

            String sortBy = "";
            if (currentSelectedMovieCategoryId == R.string.most_popular_movies_json) {
                sortBy = "popular";
            }
            if (currentSelectedMovieCategoryId == R.string.tor_rated_movies_json) {
                sortBy = "top_rated";
            }

            String baseUrl = "https://api.themoviedb.org/3/movie/" + sortBy;

            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("api_key", "???????")
                    .appendQueryParameter("page", String.valueOf(currentPage))
                    .build();
            URL url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // Log.w("******** url", url.toString());

            try {
                return getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String apiResponse) {
            mProgressBar.setVisibility(View.GONE);
            if (apiResponse!= null) {
                Log.w("api_responce", apiResponse);
            }
            if (mMovieAdapter != null && apiResponse != null) {
                showJsonDataView();
                mMovieAdapter.loadMovies(apiResponse);
            } else {
                if (page>1) {
                    page--;
                }
                if(mMovieAdapter.getMovies()==null) {
                    showErrorMessage();
                }
            }
        }

    }
}