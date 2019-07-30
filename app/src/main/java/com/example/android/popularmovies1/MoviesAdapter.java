package com.example.android.popularmovies1;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies1.model.Movie;
import com.example.android.popularmovies1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    //An onClickHandler defined to make it easy to the activity to interface with the recyclerview
    private final MoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> mMovies;

    /**
     * Creates a ForecastAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String[] posters = posterPath();
        String image = posters[position];
        //code learnt from resource3 in the reviewer note.
        Picasso.with(holder.listItemMovieView.getContext()).load(image).placeholder(R.drawable.placeholder_movie).fit().centerInside().into(holder.listItemMovieView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();

    }

    public void setMovieData(List<Movie> Movies) {
        mMovies = Movies;
        notifyDataSetChanged();
    }

    // method returns an array of Strings representing the movie posters' urls
    public String[] posterPath() {
        String[] posters = new String[mMovies.size()];
        for (int i = 0; i < posters.length; i++) {
            String poster_path = mMovies.get(i).getPoster_path();
            String imageUrl = NetworkUtils.buildImageUrl(poster_path);
            posters[i] = imageUrl;
        }
        return posters;
    }

    //the interface that recieves onClick messages
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie clickedMovie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView listItemMovieView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            listItemMovieView = (ImageView) itemView.findViewById(R.id.rv_movies_item);
            itemView.setOnClickListener(this);

        }

        // This gets called by the child views during a click.
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }


    }

}

