package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yvonne on 10/11/2015.
 */
public class MovieArrayAdapter extends ArrayAdapter<String> {

    private final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<String> mValues = new ArrayList<>();
    private List<Integer> mPosterIds;

    public MovieArrayAdapter(Context context,int layoutResId, ArrayList<String> values, List<Integer> posterIds){
        super(context,layoutResId, values);
        this.mContext = context;
        this.mValues = values;
        mPosterIds = posterIds;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null){
           LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           convertView = inflater.inflate(R.layout.movie_list_detail, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_imageView_left);
        TextView urlView = (TextView) convertView.findViewById(R.id.url);

        String url = getItem(position);
        urlView.setText(url);
        Log.i(LOG_TAG, "url string: " + url);

//        Picasso.with(getContext())
//                .load(url)
//                .into(imageView);

        //gets the drawable based on the drawable ids for the drawable.
        imageView.setImageDrawable(mContext.getResources().getDrawable(mPosterIds.get(position)));

        return convertView;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public String getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
