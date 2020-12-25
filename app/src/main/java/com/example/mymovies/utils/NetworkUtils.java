package com.example.mymovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/movie/";

    final static String PAGE_PARAM = "page";
    final static String API_KEY_PARAM = "api_key";
    final static String LANGUAGE_PARAM = "language";    
    final static String GENRES_PARAM = "with_genres";

    // Our API KEY
    private static final String apiKey = "My_API_Key";

    // An ISO 639-1 value to display translated data for the fields that support it
    private static final String languageIsoCode = "el";

    // Url to get a list of genres:  https://api.themoviedb.org/3/genre/movie/list?api_key=apiKey
    private static final String genresList = "10749,14";

    public static URL buildUrl(String sortByPath, String apiPage) {
        String baseUrl =  FORECAST_BASE_URL + sortByPath;

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(PAGE_PARAM, apiPage)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                //.appendQueryParameter(LANGUAGE_PARAM, languageIsoCode)
                //.appendQueryParameter(GENRES_PARAM, genresList)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        int timeout = 3000;
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
