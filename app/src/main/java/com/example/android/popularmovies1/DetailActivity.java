package com.example.android.popularmovies1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies1.database.AppExecutors;
import com.example.android.popularmovies1.database.Database;
import com.example.android.popularmovies1.model.Movie;
import com.example.android.popularmovies1.model.Trailer;
import com.example.android.popularmovies1.utilities.JSONUtils;
import com.example.android.popularmovies1.utilities.NetworkUtils;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Trailer[]>
                                                                 ,TrailersAdapter.TrailersAdapterOnClickHandler {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private String mMovieName;
    private ImageView mMoviePoster;
    private TextView mMovieDate;
    private TextView mMovieRating;
    private TextView mMovieOverview;
    private RecyclerView mMovieTrailers;
    private TrailersAdapter mTrailerAdapter;
    private TextView mMovieReview;
    public static final int TRAILER_LOADER_ID = 4;
    private ShineButton mFavouriteButton;
    private Database mDb;
    private boolean isFavorite;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mMovieDate = (TextView) findViewById(R.id.movie_date);
        mMoviePoster = (ImageView) findViewById(R.id.movie_poster);
        mMovieRating = (TextView) findViewById(R.id.movie_rating);
        mMovieOverview = (TextView) findViewById(R.id.movie_overview);
        mMovieTrailers =(RecyclerView) findViewById(R.id.rv_trailers);
        mMovieReview =(TextView) findViewById(R.id.load_reviews);
        mFavouriteButton = (ShineButton) findViewById(R.id.favourite_button);
        mDb = Database.getInstance(getApplicationContext());

        final Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie")) {
                movie = (Movie) intentThatStartedThisActivity.getParcelableExtra("movie");
                mMovieDate.setText(movie.getRelease_date());
                String image = NetworkUtils.buildImageUrl(movie.getPoster_path());
                Picasso.with(this).load(image).placeholder(R.drawable.placeholder_movie).fit().into(mMoviePoster);
                mMovieRating.setText(movie.getRating()+" / 10");
                mMovieOverview.setText(movie.getOverview());
                mMovieName = movie.getTitle();
                setTitle(mMovieName);
                LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mMovieTrailers.setLayoutManager(trailerLayoutManager);
                mTrailerAdapter = new TrailersAdapter(this);
                mMovieTrailers.setAdapter(mTrailerAdapter);
                getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
                mMovieReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = DetailActivity.this;
                        Class destinationClass = ReviewsActivity.class;
                        Intent intentToStartReviewsActivity = new Intent(context, destinationClass);
                        intentToStartReviewsActivity.putExtra(Intent.EXTRA_TEXT, movie.getId());
                        startActivity(intentToStartReviewsActivity);

                    }
                });
                if (MainActivity.mFavoriteMovies != null){
                    for (Movie favoriteMovie: MainActivity.mFavoriteMovies){
                        if (movie.getId().equals(favoriteMovie.getId())){
                            isFavorite=true;
                            break;

                        }
                    }
                }
                if (isFavorite){
                    mFavouriteButton.setChecked(true);
                }
                setUpFavorite(movie);
            }
        }

    }
    private void setUpFavorite (final Movie movie){
        //code from Resource5 in the reviewer notes
        mFavouriteButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, final boolean checked) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                            if (checked) {
                                mDb.favoritesDao().insertMovie(movie);
                            } else {
                                mDb.favoritesDao().deleteMovie(movie);
                            }
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Loader<Trailer[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<Trailer[]>(this) {
            @Override
            public void onStartLoading(){
                super.onStartLoading();
                forceLoad();
            }
            @Nullable
            @Override
            public Trailer[] loadInBackground() {
                URL trailersUrl = NetworkUtils.buildTrailerURL(movie.getId());
                String results = null;
                try {
                    results = NetworkUtils.getResponseFromHttpUrl(trailersUrl);
                    Trailer[] trailers_list;
                    trailers_list = JSONUtils.parseTrailerJson(results);
                    return trailers_list;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Trailer[]> loader, Trailer[] trailers_list) {
        if (trailers_list != null) {
            mTrailerAdapter.setTrailerData(trailers_list);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Trailer[]> loader) {

    }

//Code from Resource3 in the reviewer notes
    @Override
    public void onClick(Trailer clickedTrailer) {
        String key = clickedTrailer.getKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key));
        try
        {
            startActivity(appIntent);
        }

        catch(ActivityNotFoundException ex)
        {
            startActivity(webIntent);

        }
    }
}