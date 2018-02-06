package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Aaron on 1/30/2018.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Movie[] mMovieList;
    private MovieAdapterOnClickHandler mClickHandler;
    private final String TAG = "Customer Cursor";

    public CustomCursorAdapter(){}
    public CustomCursorAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        Log.v(TAG, "Create customer view");
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
        Log.v(TAG, "Position" + position + "path" + posterPath);

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
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        Log.v(TAG, "get item," + mCursor.getCount());
        return mCursor.getCount();
    }

    public interface MovieAdapterOnClickHandler {
        void onListItemClick(Movie movieItem);
    }

}
