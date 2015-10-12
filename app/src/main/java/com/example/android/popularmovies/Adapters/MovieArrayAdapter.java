package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.DiscoveryActivityFragment;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yvonne on 10/11/2015.
 */
public class MovieArrayAdapter extends ArrayAdapter<String> {

    private final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    private final Context context;
    private final ArrayList<String> values;

    public MovieArrayAdapter(Context context,int layoutResId, ArrayList<String> values){
        super(context, layoutResId, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


       View rowView = inflater.inflate(R.layout.movie_list_detail, parent, false);
        if(context ==null){
            Log.e(LOG_TAG,"Context is null");
        }
        ImageView imageView = (ImageView) rowView.findViewById(R.id.movie_imageView_left);

        Log.i(LOG_TAG, "url string: "+ values.get(position));

        Picasso.with(this.context)
                .load(values.get(position))
                .into(imageView);

        return convertView;
    }
}
