package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymovies.model.Movie;
import com.squareup.picasso.Picasso;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MovieDetailsActivity extends AppCompatActivity {
    Movie mMovie;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovie =  getIntent().getParcelableExtra("movie");

        ImageView backdropImageView = findViewById(R.id.backdrop_image);
        Picasso.get().load(mMovie.getBackdropPath()).into(backdropImageView);

        ImageView posterImageView = findViewById(R.id.poster_image);
        Picasso.get().load(mMovie.getPosterPath()).into(posterImageView);

        TextView originalTitle = findViewById(R.id.original_title);
        originalTitle.setText(mMovie.getOriginalTitle());

        TextView overview = findViewById(R.id.overview);
        overview.setText(mMovie.getOverview());
        if (Build.VERSION.SDK_INT >= 26) {
            overview.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView voteAverage = findViewById(R.id.vote_average);
        voteAverage.setText(mMovie.getVoteAverage() + "/10");

        TextView releaseDate = findViewById(R.id.release_date);
        String releaseDateString =  mMovie.getReleaseDate();
        if (releaseDateString.length()>0) {
            releaseDate.setText(releaseDateString.substring(0, 4));
        } else  {
            releaseDate.setText("");
        }
    }
}