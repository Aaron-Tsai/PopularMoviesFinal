package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    //private TextView mMovieTitle;
    //private TextView mReleaseDate;
    //private TextView mOverview;
    //private TextView mVoteAverage;
    //private ImageView mMoviePoster;

    /**
     * This activity is started by an intent being launched in the Main Activity. It takes the information that was packaged with
     * the intent and uses it to populate its own layout items.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ImageView moviePosterView = findViewById(R.id.details_activity_movie_poster);
        TextView movieTitleView = findViewById(R.id.details_activity_movie_title);
        TextView releaseDateView = findViewById(R.id.details_activity_release_date);
        TextView overviewView = findViewById(R.id.details_activity_overview);
        TextView voteAverageView = findViewById(R.id.details_activity_vote_average);

        Intent activityStarter = getIntent();

        String posterPath = activityStarter.getStringExtra(getString(R.string.poster_path));
        Picasso.with(this).load(getString(R.string.poster_url_base_path) + posterPath).into(moviePosterView);

        String movieTitle = activityStarter.getStringExtra(getString(R.string.movie_title));
        movieTitleView.setText(movieTitle);

        String releaseDate = activityStarter.getStringExtra(getString(R.string.release_date));
        releaseDateView.setText(releaseDate);

        String overview = activityStarter.getStringExtra(getString(R.string.overview));
        overviewView.setText(overview);

        String voteAverage = String.valueOf(activityStarter.getDoubleExtra(getString(R.string.vote_average), 0));
        voteAverageView.setText(voteAverage);

    }

}
