package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Movie[] mMovies;
    private final Context mContext;
    View.OnClickListener listener;

    public ImageAdapter(Context mContext, Movie[] mMovies) {
        this.mMovies = mMovies;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
        }
    }
    @NonNull
    @Override
    // Create new views (Invoked by the Layout Manager)
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext ())
                .inflate (R.layout.image_thumb_view, parent, false);

        ViewHolder vh = new ViewHolder (v);
        return vh;
    }

    @Override
    // Replace the contents of a view (Invoked by the layout manager)
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Picasso.get()
                .load(mMovies[position].getPosterPath())
                .fit()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into((ImageView) holder.mImageView.findViewById (R.id.image_view));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("movie", mMovies[position]);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMovies == null || mMovies.length == 0) {
            return -1;
        }

        return mMovies.length;
    }
}
