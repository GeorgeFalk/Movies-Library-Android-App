package com.georgefalk.moviesnew2018;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    public static ArrayList<String> test;



    //for recycler and sql View on mainactivity
    private MySQLdbHelper mySQLdbHelper;
    public static ArrayList<Movie> movieArrayList;
    private ArrayList<String> movieNames = new ArrayList<>();
    private ArrayList<String> moviePlots = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<String> movieYears = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;
    Cursor movieData;




    public final static String TAG = "check";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inits for recycler view
        movieArrayList = new ArrayList<>();
        test = new ArrayList<>();
        recyclerView = findViewById(R.id.movies_list);

         //inits for sqlite database
        mySQLdbHelper = new MySQLdbHelper(this);
        movieData = mySQLdbHelper.getAllData();
        getMoviesToArray(movieData);

        initRecycler();

        Log.d(TAG, "onCreate: movieData.getCount() " + movieData.getCount());

    }

    //fill array and recycler view called from mainactivity
    public void initRecycler() {

        movieNames.clear();
        moviePlots.clear();
        imageUrls.clear();
        movieYears.clear();

        if (movieArrayList.size() > 0) {
            for (int i = 0; i < movieArrayList.size(); i++) {
                movieNames.add(movieArrayList.get(i).getName());
                moviePlots.add(movieArrayList.get(i).getBody());
                imageUrls.add(movieArrayList.get(i).getUrl());
                movieYears.add(movieArrayList.get(i).getYear());

            }

        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, movieNames, moviePlots, movieYears, imageUrls);
        recyclerViewAdapter.setListener(new RecyclerViewAdapter.Listener() {

            // short click to edit movie
            @Override
            public void onClickRecyclerListener(int position) {
                Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
                intent.putExtra(Agreements.MOVIENAME, movieNames.get(position));
                intent.putExtra(Agreements.MOVIEPLOT, moviePlots.get(position));
                intent.putExtra(Agreements.MOVIEYEAR, movieYears.get(position));
                intent.putExtra(Agreements.URLMOVIE, imageUrls.get(position));
                intent.putExtra(Agreements.POSITION, position);
                intent.putExtra(Agreements.ID, movieArrayList.get(position).getId());
                startActivity(intent);
                finish();
                Log.d(TAG, "onClickRecyclerListener: ");
            }
        });

        recyclerViewAdapter.setLongListener(new RecyclerViewAdapter.LongListener() {
            @Override
            public void onLongRecyclerClick(final int position) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.delete_dialog);
                 Button deleteButton = dialog.findViewById(R.id.delete_movie_button);
                 Button cancel = dialog.findViewById(R.id.cancel_movie_button);

                 deleteButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         deleteMovie(position);
                         dialog.dismiss();

                     }
                 });

                 cancel.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         dialog.dismiss();

                     }
                 });

                 dialog.show();

            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //create options in right up corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteMovie(int position) {
        mySQLdbHelper.deleteMovie(movieArrayList.get(position).getId());
        movieArrayList.remove(position);
        initRecycler();
        recyclerViewAdapter.notifyDataSetChanged();

    }

    //FAB button clicked to add movie either from web or manually. Shows up in dialog
    public void onAddClicked(View view) {
       final Dialog dialog = new Dialog(this);
       dialog.setContentView(R.layout.add_dialog);

       Button  addFromWebButton = dialog.findViewById(R.id.add_from_web_button);
       Button addManuallyButton = dialog.findViewById(R.id.add_mannualy_button);


       addFromWebButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this, MovieFromWebActivity.class);
               startActivity(intent);
               dialog.dismiss();
               finish();

           }
       });

       addManuallyButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
               startActivity(intent);
               dialog.dismiss();
               finish();

           }
       });
      dialog.show();

    }

    // fill movies objects from database to array
    public void getMoviesToArray(Cursor cursor) {
        String name = "";
        String body = "";
        String url = "";
        String id="";
        String year="";

        while (cursor.moveToNext()) {

            id = ""+ cursor.getString(0);
            name = "" + cursor.getString(1);
            body = "" + cursor.getString(2);
            url = "" + cursor.getString(3);
            year = "" + cursor.getString(4);
            Movie movie = new Movie(id, name, body, url, year);
            movieArrayList.add(movie);

        }
    }



    public void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void clearAll(MenuItem item) {
         mySQLdbHelper.resetDatabase();
         movieNames.clear();
         moviePlots.clear();
         imageUrls.clear();
         movieYears.clear();
         recyclerViewAdapter.notifyDataSetChanged();
         mySQLdbHelper.resetDatabase();
         Snackbar.make(findViewById(R.id.main_layout), "All movies were deleted!", Snackbar.LENGTH_SHORT).show();

    }



    public void helpClicked(MenuItem item) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_dialog);
        dialog.show();
    }
}
