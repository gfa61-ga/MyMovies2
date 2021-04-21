package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mymovies.model.Movie;
import com.example.mymovies.model.Trailer;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.LayoutUtils;
import com.squareup.picasso.Picasso;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.OnClickHandler, ReviewAdapter.OnClickHandler{
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
            overview.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
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

        Context context = this;

        int numOfColumns = LayoutUtils.calculateNoOfTrailerColumns(getApplicationContext());
        RecyclerView trailersDisplayRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, numOfColumns);
        mGridLayoutManager.setReverseLayout(false);

        trailersDisplayRecyclerView.setLayoutManager(mGridLayoutManager);
        trailersDisplayRecyclerView.setHasFixedSize(true);

        TrailerAdapter.OnClickHandler mClickHandler = this;
        TrailerAdapter trailersAdapter = new TrailerAdapter(mClickHandler, mGridLayoutManager);
        trailersAdapter.setTrailers(JsonUtils.parseTrailers(getResources().getString(R.string.trailers_json)));
        //trailersAdapter.setReviews(JsonUtils.parseReviews(getResources().getString(R.string.reviews_json)));
        trailersDisplayRecyclerView.setAdapter(trailersAdapter);

        ReviewAdapter.OnClickHandler mClickHandler1 = this;
        RecyclerView reviewsDisplayRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager mGridLayoutManager1 = new LinearLayoutManager (context);
        reviewsDisplayRecyclerView.setLayoutManager(mGridLayoutManager1);
        reviewsDisplayRecyclerView.setHasFixedSize(true);


        ReviewAdapter reviewsAdapter = new ReviewAdapter(mClickHandler1, mGridLayoutManager1);
        //reviewsAdapter.setTrailers(JsonUtils.parseTrailers(getResources().getString(R.string.trailers_json)));
        reviewsAdapter.setReviews(JsonUtils.parseReviews(getResources().getString(R.string.reviews_json)));
        reviewsDisplayRecyclerView.setAdapter(reviewsAdapter);


    }



    @Override
    public void onClick(Trailer trailer) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v="+ trailer.getKey()));
        try {
            MovieDetailsActivity.this.startActivity(webIntent);
        } catch (ActivityNotFoundException ex) {
        }
    }

}