package com.example.mymovies;

import com.example.mymovies.model.Movie;
import com.squareup.picasso.Picasso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            moviePosterImageView = (ImageView) itemView.findViewById(R.id.movie_item_poster);
        }
    }
    public void notifyAdapterDataSetChanged(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
