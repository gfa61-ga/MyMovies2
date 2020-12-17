package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private int selectedMovieCategoryId;
    private int page;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    Toast.makeText(MainActivity.this, "Last", Toast.LENGTH_LONG).show();
                    page++;
                    mMovieAdapter.readNextPage(page);
                }
            }
        });
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order_most_popular) {
            selectedMovieCategoryId = R.string.most_popular_movies_json;
            page=1;
            loadMovies();
            return true;
        }
        if (id == R.id.action_order_top_rated) {
            selectedMovieCategoryId = R.string.tor_rated_movies_json;
            page=1;
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
        String apiResponse = getResources().getString(selectedMovieCategoryId);
        if (mMovieAdapter != null) {
            mMovieAdapter.loadMovies(apiResponse);
        }
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
        mToast.show();
        launchMovieDetailsActivity(Integer.parseInt(message ));
    }
    private void launchMovieDetailsActivity(int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_INDEX, position);
        intent.putExtra("movie", mMovieAdapter.getMovies().get(position));
        startActivity(intent);
    }
}