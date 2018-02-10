package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;

import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<String[]> {

    /**
     * This activity is started by an intent being launched in the Main Activity. It takes the information that was packaged with
     * the intent and uses it to populate its own layout items.
     */

    Button trailerButton;
    TextView reviewView;
    TextView authorView;

    String youtubeBasePath = "https://www.youtube.com/watch?v=";
    private final String extras1 = "https://api.themoviedb.org/3/movie/";
    private final String extras2 = "?api_key=&append_to_response=videos,reviews";
    private static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        trailerButton = findViewById(R.id.details_activity_trailer_button);

        authorView = findViewById(R.id.details_activity_review_author);
        reviewView = findViewById(R.id.details_activity_review);

        ImageView moviePosterView = findViewById(R.id.details_activity_movie_poster);
        TextView movieTitleView = findViewById(R.id.details_activity_movie_title);
        TextView releaseDateView = findViewById(R.id.details_activity_release_date);
        TextView overviewView = findViewById(R.id.details_activity_overview);
        TextView voteAverageView = findViewById(R.id.details_activity_vote_average);

        Intent activityStarter = getIntent();

        String posterPath = activityStarter.getStringExtra(getString(R.string.poster_path));
        Picasso.with(this).load(getString(R.string.poster_url_base_path) + posterPath).into(moviePosterView);

        String movieID = String.valueOf(activityStarter.getIntExtra(getString(R.string.id), 0));

        String movieTitle = activityStarter.getStringExtra(getString(R.string.movie_title));
        movieTitleView.setText(movieTitle);

        String releaseDate = activityStarter.getStringExtra(getString(R.string.release_date));
        releaseDateView.setText(releaseDate);

        String overview = activityStarter.getStringExtra(getString(R.string.overview));
        overviewView.setText(overview);

        String voteAverage = String.valueOf(activityStarter.getDoubleExtra(getString(R.string.vote_average), 0));
        voteAverageView.setText(voteAverage);

        Button favoriteButton = findViewById(R.id.details_activity_favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavorite();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(R.string.movieID_key), movieID);
        getSupportLoaderManager().initLoader(0, bundle, this);
    }

    private Cursor searchForItem(String posterPath)
    {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null,null);
        if (cursor != null) {
            int totalRows = cursor.getCount();
            Log.v(TAG, Integer.toString(totalRows));
            if (totalRows != 0) {
                cursor.moveToFirst();
                for (int i = 0; i < totalRows; i++) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POSTER_PATH));
                    Log.v(TAG, "path="+path+",posterPath="+posterPath);
                    if (path.equals(posterPath)) {
                        cursor.close();
                        return (cursor);
                    }
                    cursor.moveToNext();
                }
            }
        }
        return null;
    }

    public void onFavorite() {

        Intent activityStarter = getIntent();
        String posterPath = activityStarter.getStringExtra(getString(R.string.poster_path));

        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        Cursor cursor=searchForItem(posterPath);

        if (cursor != null) {
            Toast.makeText(getBaseContext(), posterPath + "Already in faviorite ", Toast.LENGTH_LONG)
                    .show();
            Log.v(TAG, posterPath + "Already in faviorite ");
            return;
        }

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null) {

            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {
                String movieID = args.getString(String.valueOf(R.string.movieID_key));
                String[] extras = NetworkUtils.fetchExtras(extras1 + movieID + extras2);
                return extras;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        String youtubeID = data[0];
        final String url = youtubeBasePath + youtubeID;

        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        String author = data[1];
        String review = data[2];

        if (author != null && review != null) {
            String authorText = getString(R.string.reviewed_by) + " " + author + getString(R.string.colon);
            authorView.setText(authorText);
            reviewView.setText(review);
        } else {
            authorView.setText(R.string.no_reviews_yet);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {}
}
