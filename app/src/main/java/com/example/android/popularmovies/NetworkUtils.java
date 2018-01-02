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

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    static Movie[] fetchMovieList(String queryUrl) {
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(new URL(queryUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractMovieList(jsonResponse);
    }

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
                        movieIndex.getDouble("vote_average"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieList;
    }

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
