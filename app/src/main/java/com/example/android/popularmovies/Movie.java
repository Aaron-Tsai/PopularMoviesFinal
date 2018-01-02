package com.example.android.popularmovies;

public class Movie {
    private String mMoviePoster;
    private String mTitle;
    private String mReleaseDate;
    private String mOverview;
    private double mVoteAverage;

    public Movie(String moviePoster, String title, String releaseDate, String overview, double voteAverage) {
        mMoviePoster = moviePoster;
        mTitle = title;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mVoteAverage = voteAverage;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getReleaseDate() {
        return mReleaseDate;
    }
    public String getOverview() {
        return mOverview;
    }
    public double getVoteAverage() {
        return mVoteAverage;
    }
}

