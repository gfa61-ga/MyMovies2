package com.example.mymovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.model.Movie;
import com.example.mymovies.model.Review;
import com.example.mymovies.model.Trailer;
import com.example.mymovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/** Creates a moviesDisplayAdapter instance that
 * binds movies data to movieViewHolders that are displayed within the moviesDisplayRecyclerView.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> trailers;
    private List<Review> reviews;
    private final OnClickHandler mClickHandler;
    private final LinearLayoutManager mGridLayoutManager;

    public interface OnClickHandler {
        void onClick(Trailer trailer);
    }

    /** Through the mClickHandler parameter is passed a reference to the mainActivity instance
     * in order to call it's onClick method.
     * Through the mGridLayoutManager parameter is passed a reference to the moviesDisplayRecyclerView
     * layout in order to call it's getSpanCount method
     */
    public TrailerAdapter(OnClickHandler mClickHandler, LinearLayoutManager mGridLayoutManager) {
        this.mClickHandler = mClickHandler;
        this.mGridLayoutManager = mGridLayoutManager;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToRoot = false;

        View itemView = inflater.inflate(R.layout.trailer_list_item, parent, attachToRoot);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Picasso.get().load("https://img.youtube.com/vi/"+ trailers.get(position).getKey() + "/maxresdefault.jpg").resize(120,90).into(holder.trailerThumbImageView);
        holder.trailerNameTextView.setText(trailers.get(position).getName());
    }

    /** Returns the number of items (posters) to be displayed*/
    @Override
    public int getItemCount() {
        if (trailers != null) {
            return trailers.size();
        } else {
            return 0;
        }
    }
    /** Each MovieViewHolder instance contains a movie_list_item_layout view and
     * metadata about its position within the moviesDisplayRecyclerView.
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        public final ImageView trailerThumbImageView;
        public final TextView trailerNameTextView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerThumbImageView = itemView.findViewById(R.id.trailer_thumb);
            trailerNameTextView = itemView.findViewById(R.id.trailer_name);

            trailerThumbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int trailerViewHolderPosition = getBindingAdapterPosition(); // this is also movie's index in moviesList
                    // Calls mainActivity's onClick method which opens movie's detailsActivity
                    mClickHandler.onClick(trailers.get(trailerViewHolderPosition));
                }
            });
        }
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
