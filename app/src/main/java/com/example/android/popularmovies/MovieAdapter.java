package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Movie[] mMovieList;
    private MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movie_thumbnail;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movie_thumbnail = itemView.findViewById(R.id.movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movieItem = mMovieList[adapterPosition];
            mClickHandler.onListItemClick(movieItem);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }
    @Override

    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String posterPath = mMovieList[position].getMoviePoster();
        Context context = holder.itemView.getContext();
        Picasso.with(context).load(context.getString(R.string.poster_url_base_path) + posterPath).into(holder.movie_thumbnail);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.length;
    }

    public void setMovieList(Movie[] movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onListItemClick(Movie movieItem);
    }
}


