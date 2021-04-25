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

/** Creates a moviesDisplayAdapter instance that
 * binds movies data to movieViewHolders that are displayed within the moviesDisplayRecyclerView.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> moviesList;
    private final OnClickHandler mClickHandler;
    private final GridLayoutManager mGridLayoutManager;
    private String sortByApiPath;

    public interface OnClickHandler {
        void onClick(Movie movie);
    }

    /** Through the mClickHandler parameter is passed a reference to the mainActivity instance
     * in order to call it's onClick method.
     * Through the mGridLayoutManager parameter is passed a reference to the moviesDisplayRecyclerView
     * layout in order to call it's getSpanCount method
     * Through the sortByApiPath parameter is passed the sortByApiPath value of the MainActivity,
     * to be used in getItemCount() function
     */
    public MovieAdapter(OnClickHandler mClickHandler, GridLayoutManager mGridLayoutManager, String sortByApiPath) {
        this.mClickHandler = mClickHandler;
        this.mGridLayoutManager = mGridLayoutManager;
        this.sortByApiPath = sortByApiPath;
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
        // w185 is the path for getting an image with 185 dpi width
        String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

        Picasso.get().load(IMAGES_BASE_URL + moviesList.get(position).getPosterPath())
                .into(holder.moviePosterImageView);
    }

    /** Returns the number of items (posters) to be displayed*/
    @Override
    public int getItemCount() {
        // numberOfDisplayedColumns changes after screen rotation
        int numberOfDisplayedColumns = mGridLayoutManager.getSpanCount();
        if (moviesList == null) {
            return 0;
        } else if (!sortByApiPath.equals("favorites")){
            // Calculates the number of rows after each screen rotation,
            // so that the last row of posters if full.
            // The remaining posters are not displayed, but corresponding movies data
            // is kept in moviesList and displayed in next rotation if possible.
            int numberOfFullRows = moviesList.size()/numberOfDisplayedColumns; // Integer part of division
            return numberOfFullRows*numberOfDisplayedColumns;
        } else {
            return moviesList.size(); // returns all favorite movies number - The last row of posters will NOT be full
        }
    }

    /** Each MovieViewHolder instance contains a movie_list_item_layout view and
     * metadata about its position within the moviesDisplayRecyclerView.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final ImageView moviePosterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.movie_item_poster);

            moviePosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int movieViewHolderPosition = getBindingAdapterPosition(); // this is also movie's index in moviesList
                    // Calls mainActivity's onClick method which opens movie's detailsActivity
                    mClickHandler.onClick(moviesList.get(movieViewHolderPosition));
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
        notifyDataSetChanged(); // Adjusts the moviesDisplayRecyclerView to display the newly loaded movies
    }

    public List<Movie> getMovies() {
        return moviesList;
    }

    public void setMovies(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    public void setSortByApiPath(String sortByApiPath) {
        this.sortByApiPath = sortByApiPath;
    }
}
