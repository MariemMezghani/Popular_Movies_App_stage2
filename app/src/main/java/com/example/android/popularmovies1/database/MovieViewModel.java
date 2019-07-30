package com.example.android.popularmovies1.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies1.model.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = MovieViewModel.class.getSimpleName();
    // movies member variable for a list of Movie objects wrapped in a LiveData
    private LiveData<List<Movie>> favoriteMovies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        //use the loadFavorites of the FavoritesDao to initialize the movies variable
        Database database = Database.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the movies from the DataBase");
        favoriteMovies = database.favoritesDao().loadFavorites();
    }

    // a getter for the movies variable
    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
