package com.example.android.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies1.model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private Review[] mReviews;

    public ReviewsAdapter() {
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder reviewViewHolder, int position) {
        SpannableString author = new SpannableString(mReviews[position].getAuthor() + ":");
        author.setSpan(new UnderlineSpan(), 0, author.length(), 0);
        reviewViewHolder.listItemAuthorView.setText(author);
        reviewViewHolder.listItemContentView.setText(mReviews[position].getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.length;
    }

    public void setmReviews(Review[] reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public final TextView listItemAuthorView;
        public final TextView listItemContentView;
        public final TextView listItemNoReviewsView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemAuthorView = (TextView) itemView.findViewById(R.id.rv_author);
            listItemContentView = (TextView) itemView.findViewById(R.id.rv_content);
            listItemNoReviewsView = (TextView) itemView.findViewById(R.id.rv_no_reviews);

        }
    }
}
