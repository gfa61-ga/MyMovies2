package com.example.mymovies;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> movies;
    Context context;
    private MovieAdapterOnClickHandler mClickHandler ;

    interface MovieAdapterOnClickHandler {
        void onClick(String message);
    }
    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Picasso.get().load(movies.get(position).getPosterPath()).into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        } else {
            return movies.size();
        }
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView moviePosterImageView;
        public MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.movie_item_poster);
            moviePosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onClick(String.valueOf(getAdapterPosition()));
                }
            });
        }
    }

    public void loadMovies(String apiResponse) {
        List<Movie> newMovies = JsonUtils.parseMovies(apiResponse);
        if (movies == null) {
            movies = newMovies;
        } else {
            movies.addAll(newMovies);
        }
        Log.w("*************loadMonies","movies loaded from API");
        notifyDataSetChanged();
    }

    public void readNextPage(int page) { //TODO read more movies from API
        List<Movie> newMovies = new ArrayList<>(movies.subList(0,20));
        Log.w("******************page", String.valueOf(page));
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

}
