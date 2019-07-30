package com.example.android.popularmovies1.utilities;

import com.example.android.popularmovies1.model.Movie;
import com.example.android.popularmovies1.model.Review;
import com.example.android.popularmovies1.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JSONUtils {
    public static Review[] parseReviewJson(String json) {
        try {
            //convert json string to json object
            JSONObject reviewJsonObject = new JSONObject(json);
            //getting JSON Array node
            JSONArray reviews = reviewJsonObject.getJSONArray("results");
            Review[] review_list = new Review[reviews.length()];
            //looping through all reviews
            for (int i = 0; i < reviews.length(); i++) {
                JSONObject c = reviews.getJSONObject(i);
                String author = c.getString("author");
                String content = c.getString("content");
                Review review = new Review(author, content);
                review_list[i] = review;
            }
            return review_list;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Trailer[] parseTrailerJson(String json) {

        try {
            //convert json string to json object
            JSONObject reviewJsonObject = new JSONObject(json);
            //getting JSON Array node
            JSONArray trailers = reviewJsonObject.getJSONArray("results");
            Trailer[] trailer_list = new Trailer[trailers.length()];
            //looping through all reviews
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject c = trailers.getJSONObject(i);
                String key = c.getString("key");
                String name = c.getString("name");
                Trailer trailer = new Trailer(key, name);
                trailer_list[i] = trailer;
            }
            return trailer_list;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Movie> parseMovieJson(String json) {

        try {
            //convert json string to json object
            JSONObject moviesJsonObject = new JSONObject(json);
            //getting JSON Array node (Resource1)
            JSONArray movies = moviesJsonObject.getJSONArray("results");
            List<Movie> movies_list = new ArrayList<>();
            //looping through all movies
            for (int i = 0; i < movies.length(); i++) {
                JSONObject c = movies.getJSONObject(i);
                String id = c.getString("id");
                String title = c.getString("original_title");
                String image = c.getString("poster_path");
                String release_date = c.getString("release_date");
                String rating = c.getString("vote_average");
                String overview = c.getString("overview");
                Movie movie = new Movie(id, title, image, release_date, rating, overview);
                movies_list.add(movie);

            }
            return movies_list;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;


        }
    }
}
