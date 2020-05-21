package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TextWithValueListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "TextListWIthValueAdapte";

    Context context;
    String[] listTitle;
    String[] listValue;
    int listContainerLayoutResourceId; //R.layout.layout_id
    int textViewTitleId; //R.id.textview_id
    int textViewValueId; //R.id.textview_id

    public TextWithValueListAdapter(Context context, int listContainerLayoutResourceId, int textViewTitleId,
                                    int textViewValueId, String[] listTitle, String[] listValue) {
        super(context, listContainerLayoutResourceId, textViewTitleId, listTitle);

        Log.d(TAG, "TextListWIthValueAdapter: instantiate");

        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.textViewTitleId = textViewTitleId;
        this.textViewValueId = textViewValueId;
        this.listTitle = listTitle;
        this.listValue = listValue;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for TextWithValueListAdapter");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        TextView textViewTitle = (TextView) row.findViewById(textViewTitleId);
        TextView textViewValue = (TextView) row.findViewById(textViewValueId);

        textViewTitle.setText(Html.fromHtml(listTitle[position]));
        textViewValue.setText(Html.fromHtml(listValue[position]));

        return row;
    }
}