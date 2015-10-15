package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    private final static String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    public DetailsActivityFragment() {
        //set the menu to the fragment to let the fragment know that it has a menu.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null){

            ((ImageView) rootView.findViewById(R.id.posterImageView)).setImageResource(R.drawable.movie_poster_one);
            ((TextView) rootView.findViewById(R.id.titleTextView)).setText(intent.getStringExtra("title"));
            ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(intent.getStringExtra("release_date"));
            ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(intent.getStringExtra("rating"));
            ((TextView) rootView.findViewById(R.id.synopsisTextView)).setText(intent.getStringExtra("synopsis"));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_details, menu);

    }
}
