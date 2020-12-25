package com.example.mymovies;

import com.example.mymovies.model.Movie;
import com.example.mymovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> moviesList;
    private final OnClickHandler mClickHandler;
    private final GridLayoutManager mGridLayoutManager;

    public interface OnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(OnClickHandler mClickHandler, GridLayoutManager mGridLayoutManager) {
        this.mClickHandler = mClickHandler;
        this.mGridLayoutManager = mGridLayoutManager;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToRoot = false;

        View itemView = inflater.inflate(R.layout.movie_list_item, parent, attachToRoot);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Picasso.get().load(moviesList.get(position).getPosterPath()).into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        int mNoOfColumns = mGridLayoutManager.getSpanCount();
        if (moviesList == null) {
            return 0;
        } else {
            return moviesList.size()/mNoOfColumns*mNoOfColumns;
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final ImageView moviePosterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.movie_item_poster);

            moviePosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onClick(moviesList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void loadMoreMovies(String apiResponse) {
        List<Movie> moreMoviesList = JsonUtils.parseMovies(apiResponse);
        if (moviesList == null) {
            moviesList = moreMoviesList;
        } else {
            moviesList.addAll(moreMoviesList);
        }
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return moviesList;
    }

    public void setMovies(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }
}
