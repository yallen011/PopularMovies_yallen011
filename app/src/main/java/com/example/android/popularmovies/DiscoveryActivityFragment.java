package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.example.android.popularmovies.Adapters.MovieArrayAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryActivityFragment extends Fragment {

    private final String LOG_TAG = DiscoveryActivityFragment.class.getSimpleName();

    private MovieArrayAdapter mMovieAdapter;
    private ArrayList<String> movies;

    public DiscoveryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_discovery, container, false);

        FetchMovieImageTask fetchMovieImageTask = new FetchMovieImageTask();
        fetchMovieImageTask.execute();

        mMovieAdapter = new MovieArrayAdapter(
                getContext(),
                R.layout.movie_list_detail,
               movies
            );

        ListView movieListView = (ListView) rootView.findViewById(R.id.moviesListView);
        movieListView.setAdapter(mMovieAdapter);



        return rootView;
    }

    public class FetchMovieImageTask extends AsyncTask<Void,Void,List<FetchMovieImageTask.MovieInfo>>{

        private final String LOG_TAG = FetchMovieImageTask.class.getSimpleName();


        /**
         * Retrieve most popular movies playing currently from themoviedb
         * @param params
         * @return String[]
         */
        @Override
        protected List<MovieInfo> doInBackground(Void... params) {

            //this line should be uncommented after implementing the settings functions
            /*if(params.length == 0){
                Log.e(LOG_TAG, "No data passed");
                return null;
            }*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //will contain the json response from themoviedb
            String movieDBStr = null;
            String api_key="1bd9edcd2bb62b1d3edf272737ec91d3";
            String language = "en";

            try{

                //Construct movie db url
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";
                final String QUERY_PARAM = "movie";
                final String NOW_PLAYING_PARAM = "now_playing";
                final String API_KEY = "api_key";
                final String LANGUAGE_PARAM="language";

                //uri to query for movies that are now playing
                Uri movieURI = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(QUERY_PARAM)
                        .appendPath(NOW_PLAYING_PARAM)
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .appendQueryParameter(API_KEY,api_key)
                        .build();

                String movieURLStr = movieURI.toString();
                Log.i(LOG_TAG,"movie uri: " + movieURLStr);

                URL movieUrl = new URL(movieURLStr);

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) movieUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDBStr = buffer.toString();

                Log.i(LOG_TAG, "movie JSON String: " + movieDBStr);


            }catch(IOException io){

                Log.e(LOG_TAG, "Error ", io);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;

            }finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return getMovieDataFromJSON(movieDBStr);

            }catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfo) {

            //add images the listview
            /*String posterStr;
            String posterUrl;*/
            mMovieAdapter.clear();

            for(MovieInfo info : movieInfo){

               /* posterStr =  info.getPoster();
                posterUrl = buildPosterUri(posterStr);*/
                movies.add(info.getPoster());
            }

        }

        /**
         * Code to generate movie thumbnail url
         * @param poster
         * @return String
         */
        private String buildPosterUri(String poster){

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String size ="w185";

            //url for movie poster
            Uri posterUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(size)
                    .appendPath(poster)
                    .build();

            String posterURLStr = posterUri.toString();
            Log.i(LOG_TAG, "poster url: " + posterURLStr);

            return posterURLStr;
        }

        private List<MovieInfo> getMovieDataFromJSON(String movieDBJsonStr) throws JSONException {

            String[] movieArray = null;

            final String MOVIE_RESULTS = "results";
            final String ORIGINAL_TITLE ="original_title";
            final String OVERVIEW="overview";
            final String VOTE_AVERAGE="vote_average";
            final String RELEASE_DATE="release_date";
            final String POSTER_PATH="poster_path";

            JSONObject movieJson = new JSONObject(movieDBJsonStr);
            JSONArray movieResultsArray = movieJson.getJSONArray(MOVIE_RESULTS);

            List<MovieInfo> resultsList = new ArrayList<MovieInfo>();
            for (int i=0; i < movieResultsArray.length(); i++){

                JSONObject result = movieResultsArray.getJSONObject(i);

                String title = result.getString(ORIGINAL_TITLE);
                String synopsis = result.getString(OVERVIEW);
                String releaseDate = result.getString(RELEASE_DATE);
                String posterPath = result.getString(POSTER_PATH);
                String voteAvg = result.getString(VOTE_AVERAGE);

                MovieInfo movieInfo = new MovieInfo();
                movieInfo.setTitle(title);
                movieInfo.setSynopsis(synopsis);
                movieInfo.setReleaseDate(releaseDate);
                movieInfo.setPoster(buildPosterUri(posterPath));
                movieInfo.setVoteAverage(voteAvg);

                resultsList.add(movieInfo);
            }

            return resultsList;
        }

        //class to store movie data
        protected class MovieInfo{

            String title;
            String synopsis;
            String voteAverage;
            String releaseDate;
            String poster;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSynopsis() {
                return synopsis;
            }

            public void setSynopsis(String synopsis) {
                this.synopsis = synopsis;
            }

            public String getVoteAverage() {
                return voteAverage;
            }

            public void setVoteAverage(String voteAverage) {
                this.voteAverage = voteAverage;
            }

            public String getReleaseDate() {
                return releaseDate;
            }

            public void setReleaseDate(String releaseDate) {
                this.releaseDate = releaseDate;
            }

            public String getPoster() {
                return poster;
            }

            public void setPoster(String poster) {
                this.poster = poster;
            }

        }
    }
}
