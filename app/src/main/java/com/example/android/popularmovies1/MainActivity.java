package com.example.android.popularmovies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies1.database.Database;
import com.example.android.popularmovies1.database.MovieViewModel;
import com.example.android.popularmovies1.model.Movie;
import com.example.android.popularmovies1.utilities.JSONUtils;
import com.example.android.popularmovies1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final int MOVIE_LOADER_ID = 24;
    private static final String SORT_TYPE_TEXT_KEY = "sort type";
    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    public static List<Movie> mFavoriteMovies;
    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mMoviesList;
    private TextView mErrorMessage;
    private Menu menu;
    private boolean popular = true;
    private boolean top_rated = false;
    private boolean favorites = false;
    private String mSortType;
    // member variable for the database
    private Database mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessage = (TextView) findViewById(R.id.error_message);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);
        if (NetworkUtils.isOnline(this)) {
            mMoviesAdapter = new MoviesAdapter(this);
            mMoviesList.setAdapter(mMoviesAdapter);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            showErrorMessage();
        }
        mDb = Database.getInstance(getApplicationContext());
        setUpViewModel();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_TYPE_TEXT_KEY)) {
                mSortType = savedInstanceState.getString(SORT_TYPE_TEXT_KEY);
                setTitle(mSortType);
            }
        } else {
            setTitle("Most Popular Movies");
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_TYPE_TEXT_KEY, mSortType);


    }

    private void setUpViewModel() {
        if (mFavoriteMovies == null) {
            mFavoriteMovies = new ArrayList<>();
        }

        //Declare a ViewModel variable and initialize it by calling ViewModelProviders.of
        MovieViewModel viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        viewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> favoritesList) {
                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                mFavoriteMovies.clear();
                mFavoriteMovies.addAll(favoritesList);

            }
        });
    }

    //show error message and hide the recyclerview
    private void showErrorMessage() {
        mMoviesList.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movie The movie that was clicked
     */
    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popular) {
            mSortType = "Most Popular Movies";
            popular = true;
            top_rated = false;
            favorites = false;
            mMoviesAdapter.setMovieData(null);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            setTitle(mSortType);
            return true;
        }

        if (id == R.id.sort_rating) {
            mSortType = "Top Rated Movies";
            top_rated = true;
            favorites = false;
            popular = false;
            mMoviesAdapter.setMovieData(null);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            setTitle(mSortType);

            return true;
        }
        if (id == R.id.favorites) {
            favorites = true;
            popular = false;
            top_rated = false;
            mSortType = "Favorites";
            mMoviesAdapter.setMovieData(null);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            setTitle(mSortType);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, @Nullable Bundle bundle) {

        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            public void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                URL url;
                List<Movie> movies_list;

                if (favorites) {
                    movies_list = mFavoriteMovies;
                    return movies_list;
                } else {
                    if (popular) {
                        url = NetworkUtils.buildURL("popular");

                    } else {
                        url = NetworkUtils.buildURL("top_rated");
                    }
                    String results = null;
                    try {
                        results = NetworkUtils.getResponseFromHttpUrl(url);
                        movies_list = JSONUtils.parseMovieJson(results);
                        return movies_list;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;

                    }
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies_list) {
        if (movies_list != null) {
            mMoviesAdapter.setMovieData(movies_list);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }


}




