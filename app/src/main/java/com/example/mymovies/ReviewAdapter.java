package com.example.mymovies;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.model.Review;

import java.util.List;

/** Creates a reviewsAdapter instance that
 * binds reviews data to reviewViewHolders that are displayed within the reviews RecyclerView.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;

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
        holder.authorNameTextView.setText(reviews.get(position).getAuthor());
        holder.authorNameTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // https://stackoverflow.com/questions/58278665/how-to-show-read-more-text-if-text-has-more-than-4-maxlines
        int maxCharsToDisplay = 500;
        String text = reviews.get(position).getContent();
        TextView review = holder.reviewContentTextView;

        if (text.length() > maxCharsToDisplay) {
            String sub = text.substring(0, maxCharsToDisplay);
            review.setText(Html.fromHtml(sub + "<font color='#ab47bc'> " + "seeMore" + "</font>"));

            review.setOnClickListener(view -> {
                if (!review.getText().equals(text)) {
                    review.setText(text);
                }
            });
        } else {
            review.setText(text);
        }

        if (Build.VERSION.SDK_INT >= 29) {
            review.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    /** Returns the number of items (reviews) to be displayed*/
    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        } else {
            return 0;
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public final TextView authorNameTextView;
        public final TextView reviewContentTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.review_author);
            reviewContentTextView = itemView.findViewById(R.id.review_content);
        }
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
