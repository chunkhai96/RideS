package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextOnlyListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "TextOnlyListAdapter";

    Context context;
    String[] listTitle;
    int listContainerLayoutResourceId; //R.layout.layout_id
    int textViewTitleId; //R.id.textview_id

    public TextOnlyListAdapter(Context context, int listContainerLayoutResourceId,
                               int textViewTitleId, String[] listTitle) {
        super(context, listContainerLayoutResourceId, textViewTitleId, listTitle);

        Log.d(TAG, "TextListWIthValueAdapter: instantiate");

        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.textViewTitleId = textViewTitleId;
        this.listTitle = listTitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for TextWithValueListAdapter");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        TextView textViewTitle = (TextView) row.findViewById(textViewTitleId);

        textViewTitle.setText(listTitle[position]);

        return row;
    }
}