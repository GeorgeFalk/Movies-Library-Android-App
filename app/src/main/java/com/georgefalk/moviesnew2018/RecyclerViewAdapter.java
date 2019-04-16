package com.georgefalk.moviesnew2018;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<String> movieNames;
    ArrayList<String> plots;
    ArrayList<String> imageUrls;
    ArrayList<String> years;
    Context context;
    private Listener listener;
    private LongListener longListener;

    public void setLongListener(LongListener longListener) {
        this.longListener = longListener;
    }

    public RecyclerViewAdapter(Context context, ArrayList<String> movieNames, ArrayList<String> plots, ArrayList<String> years, ArrayList<String> imageUrls) {
        this.movieNames = movieNames;
        this.plots = plots;
        this.context = context;
        this.imageUrls = imageUrls;
        this.years = years;
    }

    interface Listener {
        void onClickRecyclerListener(int position);

    }

    interface LongListener {
     void onLongRecyclerClick(int position);
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "RecyclerView";

        CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }



    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv =
                (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_image_cardview, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

       final CardView cardView = holder.cardView;
       TextView movieName = cardView.findViewById(R.id.movie_name);
       TextView moviePlot = cardView.findViewById(R.id.movie_plot);
       TextView movieYear = cardView.findViewById(R.id.movie_year);
       ImageView movieImage = cardView.findViewById(R.id.movie_image);
       movieName.setText(movieNames.get(position));
       if (plots.get(position).length() > 120) {
        moviePlot.setText(plots.get(position).substring(0, 120) + "...");
       }
       else moviePlot.setText(plots.get(position));
       movieYear.setText(years.get(position));

       try {
           Picasso.get().load("" + imageUrls.get(position)).into(movieImage);
       }

       catch (Exception e) {
           e.printStackTrace();
       }

       cardView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (listener != null) {
                   listener.onClickRecyclerListener(position);
               }
           }
       });

       cardView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               if (longListener != null) {
                   longListener.onLongRecyclerClick(position);
               }
               return true;
           }

       }) ;


    }

    @Override
    public int getItemCount() {
        return movieNames.size();
    }


}
