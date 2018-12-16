package com.example.kyuji.boostcamp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> movieList;

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemLayout;
        ImageView imgPoster;
        TextView txtTitle;
        RatingBar ratingUser;
        TextView txtPubDate;
        TextView txtDirector;
        TextView txtActor;

        MovieViewHolder(View view) {
            super(view);
            itemLayout = view.findViewById(R.id.item_layout);
            imgPoster = view.findViewById(R.id.item_img_poster);
            txtTitle = view.findViewById(R.id.item_txt_title);
            ratingUser = view.findViewById(R.id.item_rating_user);
            txtPubDate = view.findViewById(R.id.item_txt_pub_date);
            txtDirector = view.findViewById(R.id.item_txt_director);
            txtActor = view.findViewById(R.id.item_txt_actor);
        }
    }

    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        MovieViewHolder movieViewHolder = (MovieViewHolder) holder;

        Glide.with(movieViewHolder.itemView.getContext())
                .load(movieList.get(i).getImage())
                .into(movieViewHolder.imgPoster);
        movieViewHolder.txtTitle.setText(Html.fromHtml(movieList.get(i).getTitle()));
        movieViewHolder.ratingUser.setRating(movieList.get(i).getUserRating());
        movieViewHolder.txtPubDate.setText(movieList.get(i).getPubDate());
        movieViewHolder.txtDirector.setText(movieList.get(i).getDirector());
        movieViewHolder.txtActor.setText(movieList.get(i).getActor());

        movieViewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieList.get(i).getLink()));
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_list, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
