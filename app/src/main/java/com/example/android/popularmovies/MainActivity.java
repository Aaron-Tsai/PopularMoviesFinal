package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final String popularMoviesUrl = "";
    private final String topRatedMoviesUrl = "";

    private static MovieAdapter mMovieAdapter;
    private CustomCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView movieRecyclerView = findViewById(R.id.rv_movies);
        mMovieAdapter = new MovieAdapter(this);
        movieRecyclerView.setAdapter(mMovieAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieRecyclerView.setHasFixedSize(true);

        new FetchMovieListTask().execute(popularMoviesUrl);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popular:
                new FetchMovieListTask().execute(popularMoviesUrl);
                return true;
            case R.id.sort_by_top_rated:
                new FetchMovieListTask().execute(topRatedMoviesUrl);
                return true;
            case R.id.sort_by_favorites:
                RecyclerView movieRecyclerView = findViewById(R.id.rv_movies);
                mCursorAdapter = new CustomCursorAdapter();
                movieRecyclerView.setAdapter(mCursorAdapter);

                GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
                movieRecyclerView.setLayoutManager(layoutManager);
                movieRecyclerView.setHasFixedSize(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
        }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
