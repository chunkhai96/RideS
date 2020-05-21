package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawableTextListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "DrawableTextListAdapter";

    Context context;
    int[] listDrawables;
    String[] listTexts;
    int listContainerLayoutResourceId; //R.layout.layout_id
    int imageViewId; //R.id.imageview_id
    int textViewId; //R.id.textview_id

    public DrawableTextListAdapter(Context context, int listContainerLayoutResourceId, int imageViewId, int textViewId,
                                   int[] drawables, String[] texts) {
        super(context, listContainerLayoutResourceId, textViewId, texts);

        Log.d(TAG, "DrawableTextListAdapter: instantiate");
        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.textViewId = textViewId;
        this.imageViewId = imageViewId;
        this.listDrawables = drawables;
        this.listTexts = texts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for DrawableTextList");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        ImageView listImageView = (ImageView) row.findViewById(imageViewId);
        TextView listTextView = (TextView) row.findViewById(textViewId);

        listTextView.setText(listTexts[position]);
        listImageView.setImageResource(listDrawables[position]);

        return row;
    }
}