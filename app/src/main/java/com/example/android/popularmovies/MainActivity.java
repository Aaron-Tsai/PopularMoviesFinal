package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private final String popularMoviesUrl = "https://api.themoviedb.org/3/movie/popular?api_key=&language=en-US&page=1";
    private final String topRatedMoviesUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=&language=en-US&page=1";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        new FetchMovieListTask().execute(popularMoviesUrl);
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, Movie[]> {

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
        }
        return super.onOptionsItemSelected(item);
    }
}