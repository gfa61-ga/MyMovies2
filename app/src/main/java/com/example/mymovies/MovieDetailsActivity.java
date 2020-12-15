package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_INDEX = "movie_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
    }
}