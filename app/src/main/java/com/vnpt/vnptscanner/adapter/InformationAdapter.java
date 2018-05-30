package com.vnpt.vnptscanner.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.model.ResultInformation;

import java.util.List;

/**
 * Created by MinhDN on 23/11/2016.
 */

public class InformationAdapter extends ArrayAdapter<ResultInformation> {
    private Context context;
    private int resource;
    private List<ResultInformation> objects;
    public InformationAdapter(Context context, int resource, List<ResultInformation> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(this.resource, null);
        TextView txtLabel = (TextView) row.findViewById(R.id.txtLabel);
        TextView txtValue = (TextView) row.findViewById(R.id.txtValue);
        txtLabel.setText(this.objects.get(position).getLabel());
        txtValue.setText(this.objects.get(position).getValue());
        return  row;
    }


}
