package com.example.mymovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/** Creates a trailersDisplayAdapter instance that
 * binds trailer data to trailerViewHolders that are displayed within the trailers RecyclerView.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> trailers;
    private final OnClickHandler mClickHandler;

    public interface OnClickHandler {
        void onClick(Trailer trailer);
    }

    /** Through the mClickHandler parameter is passed a reference to the mainActivity instance
     * in order to call it's onClick method.
     */
    public TrailerAdapter(OnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
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
        // https://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
        final String BASIC_YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/";
        final String YOUTUBE_THUMB_QUALITY = "maxresdefault.jpg";
        int thumbsWidth = 120; // dp
        int thumbsHeight = 90; // dp

        String youtubeVideoId = trailers.get(position).getKey();
        Picasso.get().load(BASIC_YOUTUBE_THUMBNAIL_URL+ youtubeVideoId + "/" + YOUTUBE_THUMB_QUALITY)
                .resize(thumbsWidth,thumbsHeight).into(holder.trailerThumbImageView);
        holder.trailerNameTextView.setText(trailers.get(position).getName());
    }

    /** Returns the number of items (trailers) to be displayed*/
    @Override
    public int getItemCount() {
        if (trailers != null) {
            return trailers.size();
        } else {
            return 0;
        }
    }

    /** Each TrailerViewHolder instance contains a trailer_list_item layout view and
     * metadata about its position within the trailers RecyclerView.
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
                    int trailerViewHolderPosition = getBindingAdapterPosition(); // this is also trailers's index in trailersList

                    // Calls movieDetailsActivity's onClick method, which displays the movie's trailer
                    mClickHandler.onClick(trailers.get(trailerViewHolderPosition));
                }
            });
        }
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
