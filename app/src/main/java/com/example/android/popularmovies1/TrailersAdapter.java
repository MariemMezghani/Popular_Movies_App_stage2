package com.example.android.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies1.model.Trailer;
import com.example.android.popularmovies1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {
    //An onClickHandler defined to make it easy to the activity to interface with the recyclerview
    private final TrailersAdapterOnClickHandler mClickHandler;
    private Trailer[] mTrailers;

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;

    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailersViewHolder viewHolder = new TrailersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersViewHolder trailersViewHolder, int position) {
        String[] thumbnails = getTrailerThumbnail();
        String thumbnail = thumbnails[position];
        Picasso.with(trailersViewHolder.listItemTrailerView.getContext()).load(thumbnail).placeholder(R.drawable.videoplaceholder)
                .fit().centerInside().into(trailersViewHolder.listItemTrailerView);


    }

    public String[] getTrailerThumbnail() {
        String[] thumbnails = new String[mTrailers.length];
        for (int i = 0; i < thumbnails.length; i++) {
            String thumbnail_path = mTrailers[i].getKey();
            String thumbnailUrl = NetworkUtils.buildThumbnailUrl(thumbnail_path);
            thumbnails[i] = thumbnailUrl;
        }
        return thumbnails;
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        } else {
            return mTrailers.length;
        }
    }

    public void setTrailerData(Trailer[] trailers_list) {
        mTrailers = trailers_list;
        notifyDataSetChanged();
    }

    //the interface that recieves onClick messages
    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailer clickedTrailer);
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView listItemTrailerView;

        public TrailersViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemTrailerView = itemView.findViewById(R.id.trailer_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = mTrailers[adapterPosition];
            mClickHandler.onClick(trailer);
        }
    }
}
