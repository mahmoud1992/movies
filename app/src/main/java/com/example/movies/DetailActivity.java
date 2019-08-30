package com.example.movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies.model.Movie;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

//    String api_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        TextView originalTitleTV = findViewById (R.id.titleTextView);
        TextView ratingTV = findViewById (R.id.ratingTextView);
        TextView releaseDateTV = findViewById (R.id.releaseDateTextView);
        TextView overviewTV = findViewById (R.id.overviewTextView);
        ImageView posterIV = findViewById (R.id.posterImageView);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie movie = intent.getParcelableExtra("movie");

        // TITLE
        originalTitleTV.setText(movie.getOriginalTitle());
        // VOTER AVERAGE / RATING
        ratingTV.setText (String.valueOf(movie.getVoterAverage ()) + " / 10");
        // IMAGE
        Picasso.get()
                .load(movie.getPosterPath())
                .fit()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(posterIV);

        // OVERVIEW
        overviewTV.setText (movie.getOverview ());

        // RELEASE DATE
        releaseDateTV.setText (movie.getReleaseDate());
    }


    private void closeOnError() {
        finish();
        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }
}
