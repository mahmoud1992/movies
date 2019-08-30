package com.example.movies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.movies.model.Movie;
import com.example.movies.utils.JsonUtils;

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

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private final int NUM_OF_COLUMNS = 2;
    private final String POPULAR_QUERY = "popular";
    private final String TOP_RATED_QUERY = "top_rated";
    Movie[] movies;
    ImageAdapter mImageAdapter;
//    String api_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById (R.id.recycler_view);

        // Using a Grid Layout Manager
        mLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // SPINNER METHODS
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        final int currentSelection = sortSpinner.getSelectedItemPosition();

        if (isOnline () == true) {
            sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (currentSelection == i) {
                        // If most popular was selected
                        new FetchDataAsyncTask().execute(POPULAR_QUERY);
                    } else {
                        // If top rated was selected
                        new FetchDataAsyncTask().execute(TOP_RATED_QUERY);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            int duration = 100;
            Toast.makeText(getApplicationContext (), "No internet connection. App will not work until online", duration).show();
        }

    }


    public Movie[] makeMoviesDataToArray(String moviesJsonResults) throws JSONException {
        // JSON filters
        final String RESULTS = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTER_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        // Get results as an array
        JSONObject moviesJson = new JSONObject(moviesJsonResults);
        JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

        // Create array of Movie objects that stores data from the JSON string
        movies = new Movie[resultsArray.length()];

        // Go through movies one by one and get data
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setOriginalTitle(movieInfo.getString(ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(OVERVIEW));
            movies[i].setVoterAverage(movieInfo.getDouble(VOTER_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(RELEASE_DATE));
        }
        return movies;
    }

    public class FetchDataAsyncTask extends AsyncTask<String, Void, Movie[]> {
        public FetchDataAsyncTask() {
            super();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            // Holds data returned from the API
            String movieSearchResults = null;

            try {
                URL url = JsonUtils.buildUrl(params);
                movieSearchResults = JsonUtils.getResponseFromHttpUrl(url);

                if(movieSearchResults == null) {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }

            try {
                return makeMoviesDataToArray (movieSearchResults);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            return null;
        }

        protected void onPostExecute(Movie[] movies) {
            mImageAdapter = new ImageAdapter(getApplicationContext(), movies);
            mRecyclerView.setAdapter(mImageAdapter);
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        //handle click on sort settings
//
//        if (id == R.id.action_sort_settings) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            final SharedPreferences.Editor editor=sharedPreferences.edit();
//            int selected = 0;
//            sort_type = sharedPreferences.getString("sort_type", "popular");
//            if(sort_type.equals("popular"))
//                selected = 0;
//            else if(sort_type.equals("top_rated"))
//                selected = 1;
//            builder.setTitle(R.string.dialog_title);
//            builder.setSingleChoiceItems(R.array.sort_types, selected,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (which == 0)
//                                editor.putString("sort_type", "popular");
//                            else if (which == 1)
//                                editor.putString("sort_type", "top_rated");
//                        }
//                    });
//            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    //user clicked save
//                    editor.commit();
//                }
//            });
//            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    //user clicked cancel
//                }
//            });
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    //refresh activity
//                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                    startActivity(intent);
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//

}
