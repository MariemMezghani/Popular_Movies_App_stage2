package com.example.android.popularmovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies1.model.Review;
import com.example.android.popularmovies1.utilities.JSONUtils;
import com.example.android.popularmovies1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class ReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Review[]> {
    public static final int REVIEW_LOADER_ID = 2;
    String movieId;
    private RecyclerView mMovieReview;
    private ReviewsAdapter mReviewAdapter;
    private TextView mNoReviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        mMovieReview = (RecyclerView) findViewById(R.id.rv_reviews);
        mNoReviews = (TextView) findViewById(R.id.rv_no_reviews);
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                movieId = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mMovieReview.setLayoutManager(reviewLayoutManager);
                mReviewAdapter = new ReviewsAdapter();
                mMovieReview.setAdapter(mReviewAdapter);
                getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID, null, this);
            }
        }
    }

    @NonNull
    @Override
    public Loader<Review[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<Review[]>(this) {
            @Override
            public void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Nullable
            @Override
            public Review[] loadInBackground() {
                URL reviewUrl = NetworkUtils.buildReviewsURL(movieId);
                String results = null;
                try {
                    results = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                    Review[] reviews_list;
                    reviews_list = JSONUtils.parseReviewJson(results);
                    return reviews_list;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Review[]> loader, Review[] reviews) {
        if (reviews != null && reviews.length != 0) {
            mReviewAdapter.setmReviews(reviews);
        } else {
            mMovieReview.setVisibility(View.GONE);
            mNoReviews.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Review[]> loader) {

    }
}
