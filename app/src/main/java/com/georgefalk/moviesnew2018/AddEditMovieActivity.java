package com.georgefalk.moviesnew2018;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class AddEditMovieActivity extends AppCompatActivity {
    public static final String TAG = "AddEditMovieActivity";
    private MySQLdbHelper mySQLdbHelper;
    EditText movieEditName;
    EditText movieEditPlot;
    ImageView imageEditMovie;
    TextView movieYear;


    String movieName;
    String moviePlot;
    String imageUrl;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_add_edit);

        mySQLdbHelper = new MySQLdbHelper(this);

        //initialize views on this activity
        movieYear = findViewById(R.id.new_year);
        movieEditName = findViewById(R.id.new_movie_name);
        movieEditPlot = findViewById(R.id.new_plot);
        imageEditMovie = findViewById(R.id.image_edit);

        // check by position from intent either it's new movie
        if (getIntent().getIntExtra(Agreements.POSITION, -100) == -100) {
            Log.d(TAG, "onCreate:  check by position from intent either it's new movie");
            addNewMovieFromWeb();

        }

        // if no int position from intent - edit movie
        else if (getIntent().getIntExtra(Agreements.POSITION, -100) != -100) {
            Log.d(TAG, "onCreate: if no int position from intent - edit movie");
            Button button = findViewById(R.id.add_button);
            TextView title = findViewById(R.id.title_add);
            button.setText("SAVE");
            title.setText("EDIT YOUR MOVIE");
            editMovie();

        }

        if (getIntent() == null) {
            Log.d(TAG, "onCreate: intent null!" );
        }

    }


    //initialize  views in this activity to add new movie. called from onCreate
    public void addNewMovieFromWeb() {
        movieName = getIntent().getStringExtra("movieName");
        moviePlot = getIntent().getStringExtra("moviePlot");
        imageUrl = getIntent().getStringExtra("urlMovie");
        movieEditName.setText(movieName);
        movieEditPlot.setText(moviePlot);
        year = getIntent().getStringExtra("movieYear");
        movieYear.setText(year);
        Log.d(TAG, "addNewMovieFromWeb: ");

        try {
            Picasso.get().load(imageUrl).into(imageEditMovie);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //initialize views for edit movie. called from onCreate
    public void editMovie() {
        movieName = getIntent().getStringExtra("movieName");
        moviePlot = getIntent().getStringExtra("moviePlot");
        imageUrl = getIntent().getStringExtra("urlMovie");
        movieEditName.setText(movieName);
        movieEditPlot.setText(moviePlot);
        year = getIntent().getStringExtra("movieYear");
        movieYear.setText(year);
        Log.d(TAG, "editMovie: ");

        try {
            Picasso.get().load(imageUrl).into(imageEditMovie);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add selected movie to library (database)
    public void submitButtonClicked(View view) {
        Movie movie;
        //add new movie and save it to sql. check with position value either it's new movie or for edit
        // new movie not inserted position in intent (initialized in addNewMovieFromWeb method)
        if (getIntent().getIntExtra(Agreements.POSITION, -100) == -100) {
            //check the movie has name (you want user to input at least a movie name)
            if (!(movieEditName.getText().toString().equals(""))) {
                //new movie from web (does have a picture from intent)
                if (imageUrl != null) {
                    movie = new Movie(movieEditName.getText().toString(), movieEditPlot.getText().toString(), imageUrl, year);
                }
                //new movie manually (does not have a picture == null)
                else {
                    String defaultUrl = "https://png.pngtree.com/element_origin_min_pic/17/09/08/dd1d4ed0921e23693b034acc04adc9a8.jpg";
                    movie = new Movie(movieEditName.getText().toString(), movieEditPlot.getText().toString(), defaultUrl, "");
                }
                //insert movie to database
                boolean isInserted = mySQLdbHelper.isInserted(movie.getName(), movie.getBody(), movie.getUrl(), movie.getYear());

                //check if that was inserted correctly
                if (isInserted) {
                    Toast.makeText(this, "Movie was added", Toast.LENGTH_SHORT).show();
                } else if (!isInserted) {
                    Toast.makeText(this, "Something go wrong", Toast.LENGTH_SHORT).show();
                }

                //back to activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            //
            else {
                Toast.makeText(this, "Input movie name", Toast.LENGTH_SHORT).show();
            }

        }

        //edit existing movie and save
        else {
            //check the movie has name
            if (!(movieEditName.getText().toString().equals(""))) {

                String id = getIntent().getStringExtra("ID");
                String name = movieEditName.getText().toString();
                String plot = movieEditPlot.getText().toString();
                mySQLdbHelper.updateMovie(id, name, plot, imageUrl);
                Log.d(TAG, "submitButtonClicked: ID " + id);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            //if movie has no name toast "Add movie name"

            else {
                Toast.makeText(this, "Add movie name", Toast.LENGTH_SHORT).show();
            }
        }



    }

    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
