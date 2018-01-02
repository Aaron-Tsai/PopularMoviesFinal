package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ProgressBar movieProgressBar;
    ScrollView movieDetails;

    TextView mMovieTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mVoteAverage;
    ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        movieProgressBar = findViewById(R.id.movie_details_loading);
        movieDetails = findViewById(R.id.movie_details);

        showProgressBar();

        mMoviePoster = findViewById(R.id.details_activity_movie_poster);
        mMovieTitle = findViewById(R.id.details_activity_movie_title);
        mReleaseDate = findViewById(R.id.details_activity_release_date);
        mOverview = findViewById(R.id.details_activity_overview);
        mVoteAverage = findViewById(R.id.details_activity_vote_average);

        Intent activityStarter = getIntent();

        String posterPath = activityStarter.getStringExtra(getString(R.string.poster_path));
        Picasso.with(this).load(getString(R.string.poster_url_base_path) + posterPath).into(mMoviePoster);

        String movieTitle = activityStarter.getStringExtra(getString(R.string.movie_title));
        mMovieTitle.setText(movieTitle);

        String releaseDate = activityStarter.getStringExtra(getString(R.string.release_date));
        mReleaseDate.setText(releaseDate);

        String overview = activityStarter.getStringExtra(getString(R.string.overview));
        mOverview.setText(overview);

        String voteAverage = String.valueOf(activityStarter.getDoubleExtra(getString(R.string.vote_average), 0));
        mVoteAverage.setText(voteAverage);

        showMovieDetails();
    }

    public void showProgressBar() {
        movieDetails.setVisibility(View.INVISIBLE);
        movieProgressBar.setVisibility(View.VISIBLE);

    }
    public void showMovieDetails() {
        movieProgressBar.setVisibility(View.INVISIBLE);
        movieDetails.setVisibility(View.VISIBLE);
    }
}
