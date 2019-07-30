package com.example.android.popularmovies1.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.popularmovies1.model.Movie;

import java.util.List;

@Dao
public interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    LiveData<List<Movie>> loadFavorites();

    @Insert
    void insertMovie(Movie favorite);

    @Delete
    void deleteMovie(Movie favorite);
}
