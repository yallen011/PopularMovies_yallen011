package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.popularmovies.Adapters.MovieArrayAdapter;

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
import com.example.android.popularmovies.DiscoveryActivityFragment.FetchMovieImageTask.MovieInfo;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryActivityFragment extends Fragment {

    private final String LOG_TAG = DiscoveryActivityFragment.class.getSimpleName();

    private ArrayAdapter<String> mMovieAdapter;
    private ArrayList<String> movies = new ArrayList<String>();
    private List<MovieInfo> movieInfoList = new ArrayList<>();

    public DiscoveryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_discovery, container, false);

       // FetchMovieImageTask fetchMovieImageTask = new FetchMovieImageTask();
        //fetchMovieImageTask.execute();

        final GridView movieGridView = (GridView) rootView.findViewById(R.id.moviesGridView);
        createMoviesArray();

        //add images by id instead of passing in the entire array. android has problems with
        //image loading.
        List<Integer> posterIds = new ArrayList<>();
        posterIds.add(R.drawable.movie_poster_one);
        posterIds.add(R.drawable.movie_poster_two);

        mMovieAdapter = new MovieArrayAdapter(
                getContext(),
                R.layout.movie_list_detail,
                movies,
                posterIds
        );
        movieGridView.setAdapter(mMovieAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Log.i(LOG_TAG, "gridview count: "+ Integer.toString(movieGridView.getCount()));
                //get movie details for selected movie
                //MovieInfo movieInfo = movieInfoList.get(position);

                //pass movie info into DetailsActivity
                Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);

//                        detailIntent.putExtra("title", movieInfo.getTitle());
//                        detailIntent.putExtra("release_date", movieInfo.getReleaseDate());
//                        detailIntent.putExtra("rating", movieInfo.getVoteAverage());
//                        detailIntent.putExtra("synopsis", movieInfo.getSynopsis());
//                        detailIntent.putExtra("poster", movieInfo.getPoster());
                startActivity(addMovieDetailsToIntent(detailIntent));
            }
        });

        return rootView;
    }

    private Intent addMovieDetailsToIntent(Intent detailIntent) {

        String summary = "During a manned mission to Mars, Astronaut Mark Watney is presumed dead after a fierce storm and left behind by his crew."
        +"But Watney has survived and finds himself stranded and alone on the hostile planet."
        + "With only meager supplies, he must draw upon his ingenuity, wit and spirit to subsist and find a way to signal to Earth that he is alive.";

        detailIntent.putExtra("title","The Martian");
        detailIntent.putExtra("release_date", "2015-10-02");
        detailIntent.putExtra("rating", "5.5");
        detailIntent.putExtra("synopsis", summary);
        detailIntent.putExtra("poster", "poster_url");

        return detailIntent;
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
                    Log.e(LOG_TAG,"Input Stream is null; returning null");
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

                Log.e(LOG_TAG, "Error: " + io.getMessage(), io);
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
                Log.e(LOG_TAG,"Error retrieving data: " + e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfo) {

            //add images the listview
            /*String posterStr;
            String posterUrl;*/
            if (movieInfo != null) {
                mMovieAdapter.clear();

                for (MovieInfo info : movieInfo) {

               /* posterStr =  info.getPoster();
                posterUrl = buildPosterUri(posterStr);*/
                    movies.add(info.getPoster());
                }
            }
        }

        /**
         * Code to generate movie thumbnail url
         * @param poster
         * @return String
         */
        private String buildPosterUri(String poster){

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String size ="w185/";

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

            List<MovieInfo> resultsList = new ArrayList<MovieInfo>();
            try {

                JSONObject movieJson = new JSONObject(movieDBJsonStr);
                JSONArray movieResultsArray = movieJson.getJSONArray(MOVIE_RESULTS);


                for (int i = 0; i < movieResultsArray.length(); i++) {

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
            }catch(JSONException e){
                Log.e(LOG_TAG, "error parsing json: " + e);
                e.getStackTrace();
            }

            return resultsList;
        }

        //class to store movie data
        protected class MovieInfo{

            private String title;
            private String synopsis;
            private String voteAverage;
            private String releaseDate;
            private String poster;

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

    private void createMoviesArray(){

        movies.add("http://image.tmdb.org/t/p/w92//AjbENYG3b8lhYSkdrWwlhVLRPKR.jpg");
        movies.add("http://image.tmdb.org/t/p/w92//slobKil2T1ASQbItglLdGfAHJqC.jpg");

       /* movies.add("apples");
        movies.add("oranges");*/
    }
}
