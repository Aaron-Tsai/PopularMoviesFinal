package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private final String popularMoviesUrl = "https://api.themoviedb.org/3/movie/popular?api_key=&language=en-US&page=1";
    private final String topRatedMoviesUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=&language=en-US&page=1";

    private static MovieAdapter mMovieAdapter;
    private CustomCursorAdapter mCursorAdapter;

    private int mLastView = MovieContract.popularity;
    private final String LIFE_CALL_BACK = "lifecycle callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView movieRecyclerView = findViewById(R.id.rv_movies);

        mCursorAdapter = new CustomCursorAdapter();
        getSupportLoaderManager().initLoader(0, null, this);

        mMovieAdapter = new MovieAdapter(this);
        movieRecyclerView.setAdapter(mMovieAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieRecyclerView.setHasFixedSize(true);

        if  (savedInstanceState != null) {
            mLastView = savedInstanceState.getInt(LIFE_CALL_BACK);
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (mLastView != MovieContract.favorites)
                    return;

                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        }).attachToRecyclerView(movieRecyclerView);

        switch (mLastView) {
            default:
            case MovieContract.popularity:
                new FetchMovieListTask().execute(popularMoviesUrl);
                break;
            case MovieContract.top_rated:
                    new FetchMovieListTask().execute(topRatedMoviesUrl);
                    break;
            case MovieContract.favorites:
                movieRecyclerView.setAdapter(mCursorAdapter);
                break;
        }
    }

    /**
     * The async task connects to the internet and provides the recycler view adapter with an array of Movie objects.
     * The string it passes as a parameter is the Url to query the movie database. This string is passed down by doInBackground
     * to NetworkUtils.fetchMovieList, which queries the Url and returns an array of Movie objects.
     */
    public static class FetchMovieListTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... strings) {
            Movie movieList[];
            try {
                movieList = NetworkUtils.fetchMovieList(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(Movie[] m) {
            mMovieAdapter.setMovieList(m);
        }
    }

    /**
     * An intent to open the movie details activity is launched upon a list item being clicked. The intent packages data from the Movie object
     * that was passed to this method by the viewholder class's onClick method.
     */
    @Override
    public void onListItemClick(Movie movieItem) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

        intent.putExtra(getString(R.string.poster_path), movieItem.getMoviePoster());
        intent.putExtra(getString(R.string.movie_title), movieItem.getTitle());
        intent.putExtra(getString(R.string.release_date), movieItem.getReleaseDate());
        intent.putExtra(getString(R.string.overview), movieItem.getOverview());
        intent.putExtra(getString(R.string.vote_average), movieItem.getVoteAverage());
        intent.putExtra(getString(R.string.id), movieItem.getId());

        startActivity(intent);
    }

    /**
     * The options menu has two buttons that toggle the recycler view items being sorted by popular and top rated movies.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIFE_CALL_BACK, mLastView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView movieRecyclerView = findViewById(R.id.rv_movies);
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popular:
                mLastView = MovieContract.popularity;
                movieRecyclerView.setAdapter(mMovieAdapter);
                new FetchMovieListTask().execute(popularMoviesUrl);
                return true;
            case R.id.sort_by_top_rated:
                mLastView = MovieContract.top_rated;
                movieRecyclerView.setAdapter(mMovieAdapter);
                new FetchMovieListTask().execute(topRatedMoviesUrl);
                return true;
            case R.id.sort_by_favorites:
                mLastView = MovieContract.favorites;
                movieRecyclerView.setAdapter(mCursorAdapter);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
        }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
