package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymovies.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_INDEX = "movie_index";
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        movie =  getIntent().getParcelableExtra("movie");

        ImageView backdropImageView = findViewById(R.id.backdrop_image);
        Picasso.get().load(movie.getBackdropPath()).into(backdropImageView);

        ImageView posterImageView = findViewById(R.id.poster_image);
        Picasso.get().load(movie.getPosterPath()).into(posterImageView);

        TextView originalTitle = findViewById(R.id.original_title);
        originalTitle.setText(movie.getOriginalTitle());

        TextView overview = findViewById(R.id.overview);
        overview.setText(movie.getOverview());

        TextView voteAverage = findViewById(R.id.vote_average);
        voteAverage.setText(movie.getVoteAverage() + "/10");

        TextView releaseDate = findViewById(R.id.release_date);
        releaseDate.setText(movie.getReleaseDate().substring(0,4));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.w("back item id", String.valueOf(item.getItemId()));
        // Log.w("androiditem id", String.valueOf(android.R.id.home));

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}