package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.mymovies.model.Movie;
import com.example.mymovies.model.Review;
import com.example.mymovies.model.Trailer;
import com.example.mymovies.utils.JsonUtils;
import com.example.mymovies.utils.LayoutUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.OnClickHandler,
        LoaderManager.LoaderCallbacks<String[]>{
    Movie mMovie;

    String[] mTrailersAndReviewsApiResponseJson; /* Reference for Trailers and Reviews API responses
        for the current mMovie, saved in MainViewModel */
    AppDatabase favoriteMoviesDatabase;

    TrailerAdapter trailersAdapter;
    ImageView trailersBar;
    TextView trailersHeader;

    ReviewAdapter reviewsAdapter;
    ImageView reviewsBar;
    TextView reviewsHeader;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        MainViewModel mMainActivityModel = new ViewModelProvider(this).get(MainViewModel.class);

        /*  When the movieDetailsActivity is created for first time
                getTrailersAndReviewsApiResponseJson() returns null
            When the movieDetailsActivity is created after rotation
                getTrailersAndReviewsApiResponseJson() returns the previous API response*/
        mTrailersAndReviewsApiResponseJson = mMainActivityModel.getTrailersAndReviewsApiResponseJson();
        if (mTrailersAndReviewsApiResponseJson[0] != null)
        Log.w("onCreate", mTrailersAndReviewsApiResponseJson[0]);

        // Initialize member variable for the data base
        favoriteMoviesDatabase = AppDatabase.getInstance(getApplicationContext());

        trailersBar = findViewById(R.id.vertical_bar_1);
        trailersHeader = findViewById(R.id.trailers_header);

        reviewsBar = findViewById(R.id.vertical_bar_2);
        reviewsHeader = findViewById(R.id.reviews_header);

        mMovie =  getIntent().getParcelableExtra("movie");

        final boolean[] isFavorite = new boolean[1];
        Executor diskIo = AppExecutors.getInstance().diskIO();

        Button favotitesButton = findViewById(R.id.fovorites_button);
        diskIo.execute(new Runnable() {
            @Override
            public void run() {
                isFavorite[0] = (favoriteMoviesDatabase.movieDao().loadMovie(mMovie.getMovieId()) != null);
                if (isFavorite[0]) {
                    favotitesButton.setText("REMOVE FROM\nFAVORITES");
                }
            }
        });

        favotitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diskIo.execute(new Runnable() { // Update database on a new thread
                    @Override
                    public void run() {
                        if (!isFavorite[0]) {
                            favoriteMoviesDatabase.movieDao().insertMovie(mMovie);
                            isFavorite[0]=true;
                            runOnUiThread(new Runnable() { // Update the Ui on the main thread
                                @Override
                                public void run() {
                                    favotitesButton.setText("REMOVE FROM\nFAVORITES");
                                }
                            });
                        } else {
                            favoriteMoviesDatabase.movieDao().deleteMovie(mMovie);
                            isFavorite[0]=false;
                            runOnUiThread(new Runnable() { // Update the Ui on the main thread
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

        // w185 is the path for getting an image with 185 dpi width
        String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

        ImageView backdropImageView = findViewById(R.id.backdrop_image);
        Picasso.get().load(IMAGES_BASE_URL + mMovie.getBackdropPath()).into(backdropImageView);

        ImageView posterImageView = findViewById(R.id.poster_image);
        Picasso.get().load(IMAGES_BASE_URL + mMovie.getPosterPath()).into(posterImageView);

        TextView originalTitle = findViewById(R.id.original_title);
        originalTitle.setText(mMovie.getOriginalTitle());

        TextView overview = findViewById(R.id.overview);
        overview.setText(mMovie.getOverview());
        if (Build.VERSION.SDK_INT >= 29) {
            overview.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView voteAverage = findViewById(R.id.vote_average);
        voteAverage.setText(mMovie.getVoteAverage() + "/10");

        TextView releaseDate = findViewById(R.id.release_date);
        String releaseDateString =  mMovie.getReleaseDate();
        if (releaseDateString.length()>0) { // If there is a releasedate in Api response
            releaseDate.setText(releaseDateString.substring(0, 4));
        } else  {
            releaseDate.setText("");
        }

        Context context = this;

        int numOfColumns = LayoutUtils.calculateNoOfTrailerColumns(getApplicationContext());
        RecyclerView trailersDisplayRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        GridLayoutManager mTrailersGridLayoutManager = new GridLayoutManager(context, numOfColumns);
        mTrailersGridLayoutManager.setReverseLayout(false);
        trailersDisplayRecyclerView.setLayoutManager(mTrailersGridLayoutManager);
        trailersDisplayRecyclerView.setHasFixedSize(true);
        TrailerAdapter.OnClickHandler mClickHandler = this;
        trailersAdapter = new TrailerAdapter(mClickHandler);
        trailersDisplayRecyclerView.setAdapter(trailersAdapter);

        RecyclerView reviewsDisplayRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager mReviewsLinearLayoutManager = new LinearLayoutManager (context);
        reviewsDisplayRecyclerView.setLayoutManager(mReviewsLinearLayoutManager);
        reviewsDisplayRecyclerView.setHasFixedSize(true);
        reviewsAdapter = new ReviewAdapter();
        reviewsDisplayRecyclerView.setAdapter(reviewsAdapter);

        /* Create or restart
                the anonymous AsyncTaskLoader instance to load movie's trailers and reviews */
        LoaderManager mLoaderManager = getLoaderManager();
        int MOVIE_TRAILERS_AND_REVIEWS_DATA_LOADER = 233;
        Loader<String> mLoader = mLoaderManager.getLoader(MOVIE_TRAILERS_AND_REVIEWS_DATA_LOADER);
        if (mLoader == null) {
            mLoaderManager.initLoader(MOVIE_TRAILERS_AND_REVIEWS_DATA_LOADER, null, this);
            Log.w("********", "initLoader");
        } else {
            // This will restart the loader after rotation..!!!
            mLoaderManager.restartLoader(MOVIE_TRAILERS_AND_REVIEWS_DATA_LOADER, null, this);
        }
    }

    @Override
    public void onClick(Trailer trailer) {
        final String BASIC_YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(BASIC_YOUTUBE_VIDEO_URL+ trailer.getKey()));
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
                // If the MovieDetailsActivity is opend for the first time, load data from API
                if(mTrailersAndReviewsApiResponseJson[0] == null && mTrailersAndReviewsApiResponseJson[1] == null){
                    forceLoad();  /* This will ignore a previously loaded data set and load a new one.
                     We generally should only call this when the loader is started. */
                } else {
                    // Deliver response, saved in MainViewModel, after rotation
                    deliverResult(mTrailersAndReviewsApiResponseJson);

                }
            }

            @Override
            public String[] loadInBackground() {
                URL trailersUrl = NetworkUtils.buildMovieUrl(mMovie.getMovieId(), "videos");
                URL reviewsUrl = NetworkUtils.buildMovieUrl(mMovie.getMovieId(), "reviews");
                try {
                    return new String[] {
                        NetworkUtils.getResponseFromHttpUrl(trailersUrl),
                        NetworkUtils.getResponseFromHttpUrl(reviewsUrl)
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] apiResponse) {
        if (apiResponse!= null) {
            if (apiResponse[0] != null && apiResponse[0] != null) {
                // Save data to the MainViewModel
                mTrailersAndReviewsApiResponseJson[0] = apiResponse[0]; // Trailers
                mTrailersAndReviewsApiResponseJson[1] = apiResponse[1]; // Reviews

                List<Trailer> trailers = JsonUtils.parseTrailers(apiResponse[0]);
                if (trailers.size() == 0) {
                    hideTrailersHeader();
                } else {
                    trailersAdapter.setTrailers(trailers);
                    trailersAdapter.notifyDataSetChanged();
                }

                List<Review> reviews = JsonUtils.parseReviews(apiResponse[1]);
                if (reviews.size() == 0) {
                    hideReviewsHeader();
                } else {
                    reviewsAdapter.setReviews(reviews);
                    reviewsAdapter.notifyDataSetChanged();
                }
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
            hideTrailersHeader();
            hideReviewsHeader();
        }
    }

    public void hideTrailersHeader() {
        trailersBar.setVisibility(View.GONE);
        trailersHeader.setVisibility(View.GONE);
    }

    public void hideReviewsHeader() {
        reviewsBar.setVisibility(View.GONE);
        reviewsHeader.setVisibility(View.GONE);
    }
    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}