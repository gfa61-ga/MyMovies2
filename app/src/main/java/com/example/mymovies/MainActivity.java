package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.Utility;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);

        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 185);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mNoOfColumns);
        mGridLayoutManager.setReverseLayout(false);
        mGridLayoutManager.canScrollVertically();
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();
        loadMovies(R.string.most_popular_movies_json);
        mRecyclerView.setAdapter(mMovieAdapter);
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
            loadMovies(R.string.most_popular_movies_json);
            return true;
        }
        if (id == R.id.action_order_top_rated) {
            loadMovies(R.string.tor_rated_movies_json);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(int stringId) {
        // we temporary saved APIresponses to res/layout/strings.xml strings
        // to save an APIresponse to a string we must
        //    1. replace all  "  in the json object with  \"
        //    2. enclose the APIresponce json object between ""
        String apiResponse = getResources().getString(stringId);
        if (mMovieAdapter != null) {
            mMovieAdapter.notifyAdapterDataSetChanged(apiResponse);
        }
    }
}