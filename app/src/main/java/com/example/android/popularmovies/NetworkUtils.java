package com.example.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * fetchMovieList queries a Url and returns an array of Movie objects.
     */
    static Movie[] fetchMovieList(String queryUrl) {
        String jsonResponse = null;
        String trailerResponse = null;
        String reviewResponse = null;
        try {
            jsonResponse = makeHttpRequest(new URL(queryUrl));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractMovieList(jsonResponse);
    }

    /**
     * extractMovieList passes a JSON string as a parameter and parses it for data.
     * It adds each of the data points to a Movie object, and returns an array of Movie objects that have been filled with the JSON data.
     */
    private static Movie[] extractMovieList(String movieJSON) {
        Movie[] movieList = new Movie[20];
        if (TextUtils.isEmpty(movieJSON)) {return null;}
        try {
            JSONObject  baseJsonResponse = new JSONObject(movieJSON);
            JSONArray results = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < 20; i++) {
                JSONObject movieIndex = results.getJSONObject(i);
                movieList[i] = new Movie(movieIndex.getString("poster_path"),
                        movieIndex.getString("title"),
                        movieIndex.getString("release_date"),
                        movieIndex.getString("overview"),
                        movieIndex.getDouble("vote_average"),
                        movieIndex.getInt("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieList;
    }

    /**
     * The following two methods, readFromStream and makeHttpRequest, were taken directly from Udacity's Quake Report app.
     * The original source code is viewable here: https://github.com/udacity/ud843-QuakeReport/blob/lesson-four/app/src/main/java/com/example/android/quakereport/QueryUtils.java
     * makeHttpRequest connects to the internet and returns a JSON string.
     * readFromStream reads the server's JSON response and returns a JSON string (it's called from within makeHttpRequest)
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {return jsonResponse;}
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {inputStream.close();}
        }
        return jsonResponse;
    }
}
