package com.georgefalk.moviesnew2018;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieFromWebActivity extends AppCompatActivity {


    private static final String TAG = "MovieFromWebActivity";

    //vars for JSON api on tmdb
    private final String urlDomain = "https://api.themoviedb.org/3/search/movie?language=en-US&include_adult=false";
    private final String api = "32f86dabbad42c2a24549075a6c7490e";
    private final String image = "http://image.tmdb.org/t/p/w185/";

    ArrayList<Movie> movieArrayList;
    EditText inputMovie;
    RecyclerView movieRecycler;
    RecyclerViewAdapter adapter;

    //initializations for recycler View on activity
    ArrayList<String> movieNames = new ArrayList<>();
    ArrayList<String> moviePlots = new ArrayList<>();
    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> movieYears = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_from_web);

        //background transparent
        inputMovie = findViewById(R.id.input_movie_web);
        inputMovie.getBackground().setAlpha(120);

        //get a back button on action bar (mainactivity is parent defined in manifest)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init recycler
        movieRecycler = findViewById(R.id.from_web_recycler);

        if (savedInstanceState != null) {
            String s = savedInstanceState.getStringArrayList("movieNames").get(0);
            movieNames = savedInstanceState.getStringArrayList("movieNames");
            moviePlots = savedInstanceState.getStringArrayList("moviePlots");
            imageUrls = savedInstanceState.getStringArrayList("imageUrls");
            movieYears = savedInstanceState.getStringArrayList("movieYears");

            initRecyclerList();

        }




    }


    //builds a recycler view with object from exactJason method. called from exactJason method
    private void initRecyclerList() {
        adapter = new RecyclerViewAdapter(this, movieNames, moviePlots, movieYears, imageUrls);
        adapter.setListener( new RecyclerViewAdapter.Listener(

        ) {
            @Override
            public void onClickRecyclerListener(int position) {
                Intent intent = new Intent(MovieFromWebActivity.this, AddEditMovieActivity.class);
                intent.putExtra("movieName", movieNames.get(position));
                intent.putExtra("moviePlot", moviePlots.get(position));
                intent.putExtra("urlMovie", imageUrls.get(position));
                intent.putExtra("movieYear", movieYears.get(position));
                startActivity(intent);
                finish();

            }
        });
        movieRecycler.setAdapter(adapter);
        movieRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    //on find button pressed starts async JSON
    public void onFindPressed(View view) {
        //clear list for all next search
        clearAdapter();
        MyTask myTask = new MyTask();
        myTask.execute(urlDomain+"&api_key="+api+"&page=1&query="+inputMovie.getText().toString());
    }

    public void clearAdapter() {
        if (movieNames.size() > 0) {
            movieNames.clear();
            moviePlots.clear();
            imageUrls.clear();
            movieYears.clear();
            adapter.notifyDataSetChanged();
        }

    }

    class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MovieFromWebActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPreExecute: ");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            exactJason(s);
            Log.d(TAG, "onPostExecute: ");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");
            return sendHttpRequest(strings[0]);
            

        }
    }

    //starts from async doInBackground method. Returns JSON array as String
    public String sendHttpRequest(String urlLink) {

        Log.d("ASYNC", "sendHttpRequest: ");
        BufferedReader input = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        URL url = null;

        try {
            url = new URL(urlLink);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() != httpURLConnection.HTTP_OK) {
                return "HTTP CONNECTION FAILED";
            }

            input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;

            while ((line = input.readLine())!=null){
                stringBuilder.append(line+"\n");
            }


        }

        catch (MalformedURLException e) {
            e.getMessage();
            return e.getMessage();
        } catch (IOException e) {
            e.getMessage();
            return e.getMessage();
        }finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
        return stringBuilder.toString();

    }

    //start in onPostExecute from doInBackground method (return JSON array string)
    public void exactJason(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String title = object.getString("title");
                String overview = object.getString("overview");
                String posterPath = object.getString("poster_path");
                String year = "Year: " + object.getString("release_date").substring(0, 4);

                movieNames.add(title);
                moviePlots.add(overview);
                imageUrls.add(image + posterPath);
                movieYears.add(year);
                initRecyclerList();
            }

            Toast.makeText(this, "Found " + jsonArray.length() + " movies", Toast.LENGTH_SHORT).show();

        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("movieNames", movieNames);
        outState.putStringArrayList("moviePlots", moviePlots);
        outState.putStringArrayList("imageUrls", imageUrls);
        outState.putStringArrayList("movieYears", movieYears);


    }
}
