package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.database.AppDatabase;
//import com.example.mymovies.database.MovieEntry;
import com.example.mymovies.model.Movie;
import com.example.mymovies.model.Review;
import com.example.mymovies.model.Trailer;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.LayoutUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.OnClickHandler, ReviewAdapter.OnClickHandler, LoaderManager.LoaderCallbacks<String[]>{
    Movie mMovie;
    private MainViewModel mMainActivityModel;
    String[] mTrailersApiResponseJson;
    TrailerAdapter trailersAdapter;
    ReviewAdapter reviewsAdapter;

    ImageView trailersBar;
    TextView trailersHeader;

    ImageView reviewsBar;
    TextView reviewsHeader;

    AppDatabase db;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mMainActivityModel = new ViewModelProvider(this).get(MainViewModel.class);
        mTrailersApiResponseJson = mMainActivityModel.getTrailersApiResponseJson();

        // Initialize member variable for the data base
        db = AppDatabase.getInstance(getApplicationContext());

        trailersBar = findViewById(R.id.vertical_bar_1);
        trailersHeader = findViewById(R.id.trailers_header);

        reviewsBar = findViewById(R.id.vertical_bar_2);
        reviewsHeader = findViewById(R.id.reviews_header);

        mMovie =  getIntent().getParcelableExtra("movie");

        Button favotitesButton = findViewById(R.id.fovorites_button);
        Executor diskIo = AppExecutors.getInstance().diskIO();
        // call the diskIO execute method with a new Runnable and implement its run method
        diskIo.execute(new Runnable() {
            @Override
            public void run() {
                if (db.movieDao().loadMovie(mMovie.getMovieId()) != null) {
                    favotitesButton.setText("REMOVE FROM\nFAVORITES");
                }
            }
        });


        favotitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Executor diskIo = AppExecutors.getInstance().diskIO();
                diskIo.execute(new Runnable() {
                    @Override
                    public void run() {
                        String movieId = mMovie.getMovieId();

                        String originalTitle = mMovie.getOriginalTitle();

                        String posterPath = mMovie.getPosterPath().substring(Math.max(0, mMovie.getPosterPath().length()-32));

                        String backdropPath = mMovie.getBackdropPath().substring(Math.max(0, mMovie.getBackdropPath().length()-32));
                        Log.w("*********", backdropPath);
                        String overview = mMovie.getOverview();

                        String voteAverage = mMovie.getVoteAverage();

                        String releaseDate = mMovie.getReleaseDate() ;

                        Movie newFavoriteMovie= new Movie(originalTitle,  posterPath,  backdropPath,
                                overview,  voteAverage,  releaseDate, movieId);

                        if (db.movieDao().loadMovie(mMovie.getMovieId()) == null) {


                            db.movieDao().insertMovie(newFavoriteMovie);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    favotitesButton.setText("REMOVE FROM\nFAVORITES");
                                }
                            });
                        } else {
                            db.movieDao().deleteMovie(newFavoriteMovie);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    favotitesButton.setText("MARK AS\nFAVORITE");
                                }
                            });
                        }
                    }
                });



            }
        });

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
        trailersAdapter = new TrailerAdapter(mClickHandler, mGridLayoutManager);
        ReviewAdapter.OnClickHandler mClickHandler1 = this;
        reviewsAdapter = new ReviewAdapter(mClickHandler1, mGridLayoutManager);

        //trailersAdapter.setTrailers(JsonUtils.parseTrailers(getResources().getString(R.string.trailers_json)));
        LoaderManager mLoaderManager = getLoaderManager();
        Loader<String> mLoader = mLoaderManager.getLoader(233);
        if (mLoader == null) {
            mLoaderManager.initLoader(233, null, this);
            Log.w("********", "initLoader");
        } else {
            // This will restart the loader after rotation..!!!
            mLoaderManager.restartLoader(233, null, this);
        }

        trailersDisplayRecyclerView.setAdapter(trailersAdapter);

        RecyclerView reviewsDisplayRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager mGridLayoutManager1 = new LinearLayoutManager (context);
        reviewsDisplayRecyclerView.setLayoutManager(mGridLayoutManager1);
        reviewsDisplayRecyclerView.setHasFixedSize(true);

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

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String[]> onCreateLoader(int i, Bundle args) {
        return new AsyncTaskLoader<String[]>(this){

            @Override
            protected void onStartLoading() {
                super.onStartLoading();  // ???
                if(mTrailersApiResponseJson[0] == null && mTrailersApiResponseJson[1] == null){
                    forceLoad(); // This will ignore a previously loaded data set and load a new one.
                    // We generally should only call this when the loader is started.
                } else {
                    deliverResult(mTrailersApiResponseJson);
                }
            }

            @Override
            public String[] loadInBackground() {
                URL trailersUrl = NetworkUtils.buildTrailerUrl(mMovie.getMovieId());
                URL reviewsUrl = NetworkUtils.buildReviewUrl(mMovie.getMovieId());
                try {
                    return new String[] {NetworkUtils.getResponseFromHttpUrl(trailersUrl), NetworkUtils.getResponseFromHttpUrl(reviewsUrl)};
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String apiResponse[]) {
        if (apiResponse!= null) {
            if (apiResponse[0] != null && apiResponse[0] != null) {
                mTrailersApiResponseJson[0] = apiResponse[0];
                mTrailersApiResponseJson[1] = apiResponse[1];

                List<Trailer> trailers = JsonUtils.parseTrailers(apiResponse[0]);
                if (trailers.size() == 0) {
                    trailersBar.setVisibility(View.GONE);
                    trailersHeader.setVisibility(View.GONE);

                } else {
                    trailersAdapter.setTrailers(trailers);
                    trailersAdapter.notifyDataSetChanged();
                }

                List<Review> reviews = JsonUtils.parseReviews(apiResponse[1]);

                if (reviews.size() == 0) {
                    reviewsBar.setVisibility(View.GONE);
                    reviewsHeader.setVisibility(View.GONE);
                } else {
                    reviewsAdapter.setReviews(reviews);
                    reviewsAdapter.notifyDataSetChanged();
                }

            } else {

            }
        } else {
            Context toastContext = MovieDetailsActivity.this;
            String errorMessage = "    No internet connection.\n" + "Can't get trailers and reviews." + "\n    Please try again later.";
            Toast noInternetToast = Toast.makeText(toastContext, errorMessage, Toast.LENGTH_LONG);
            // https://stackoverflow.com/questions/3522023/center-text-in-a-toast-in-android

            if (noInternetToast.getView() != null ) {
                TextView v = (TextView) noInternetToast.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            noInternetToast.show();

            trailersBar.setVisibility(View.GONE);
            trailersHeader.setVisibility(View.GONE);
            reviewsBar.setVisibility(View.GONE);
            reviewsHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}