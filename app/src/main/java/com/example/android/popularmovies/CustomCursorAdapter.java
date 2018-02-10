package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MovieViewHolder> {

    /*
    The CustomerCursorAdapter populates the main layout with ViewHolders that contain images ripped from the database
     */
    private Cursor mCursor;

    public CustomCursorAdapter(){}

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String posterPath = mCursor.getString(posterPathIndex);

        holder.itemView.setTag(id);
        Picasso.with(context).load(context.getString(R.string.poster_url_base_path) + posterPath).into(holder.movie_thumbnail);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView movie_thumbnail;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movie_thumbnail = itemView.findViewById(R.id.movie_thumbnail);
        }
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {return null;}

        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {this.notifyDataSetChanged();}
        return temp;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {return 0;}
        return mCursor.getCount();
    }
}
