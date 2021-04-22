package com.example.mymovies;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.model.Review;
import com.example.mymovies.model.Trailer;
import com.example.mymovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

/** Creates a moviesDisplayAdapter instance that
 * binds movies data to movieViewHolders that are displayed within the moviesDisplayRecyclerView.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {


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
    public ReviewAdapter(OnClickHandler mClickHandler, LinearLayoutManager mGridLayoutManager) {
        this.mClickHandler = mClickHandler;
        this.mGridLayoutManager = mGridLayoutManager;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToRoot = false;

        View itemView = inflater.inflate(R.layout.review_list_item, parent, attachToRoot);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        //Picasso.get().load("https://img.youtube.com/vi/"+ reviews.get(position).getKey() + "/maxresdefault.jpg").resize(120,90).into(holder.trailerThumbImageView);
        holder.authorNameTextView.setText(reviews.get(position).getAuthor());
        holder.authorNameTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // https://stackoverflow.com/questions/58278665/how-to-show-read-more-text-if-text-has-more-than-4-maxlines
        int length = 500;
        String text = reviews.get(position).getContent();
        if (text.length() > length) {
            String sub = text.substring(0, length);
            holder.reviewContentTextView.setText(Html.fromHtml(sub + "<font color='#ab47bc'> " + "seeMore" + "</font>"));
            holder.reviewContentTextView.setOnClickListener(view -> {
                if (holder.reviewContentTextView.getText().equals(text)) {
                    holder.reviewContentTextView.setText(Html.fromHtml(sub + "<font color='#F39322'> " + "seeMore" + "</font>"));
                } else {
                    holder.reviewContentTextView.setText(text);
                }
            });
        } else {
            holder.reviewContentTextView.setText(text);
        }

        if (Build.VERSION.SDK_INT >= 26) {
            holder.reviewContentTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    /** Returns the number of items (posters) to be displayed*/
    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        } else {
            return 0;
        }
    }

    /** Each MovieViewHolder instance contains a movie_list_item_layout view and
     * metadata about its position within the moviesDisplayRecyclerView.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public final TextView authorNameTextView;
        public final TextView reviewContentTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.review_author);
            reviewContentTextView = itemView.findViewById(R.id.review_content);

            reviewContentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int trailerViewHolderPosition = getAdapterPosition(); // this is also movie's index in moviesList
                    // Calls mainActivity's onClick method which opens movie's detailsActivity
                   // mClickHandler.onClick(reviews.get(trailerViewHolderPosition));
                }
            });
        }
    }

    public void loadMoreMovies(String apiResponse) {
        List<Review> moreMoviesList = JsonUtils.parseReviews(apiResponse);
        if (reviews == null) {
            reviews = moreMoviesList;
        } else {
            reviews.addAll(moreMoviesList);
        }
        notifyDataSetChanged(); // Adjusts the moviesDisplayRecyclerView to display the newly loaded movies
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setTrailers(List<Review> reviews) {
        this.reviews = reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
