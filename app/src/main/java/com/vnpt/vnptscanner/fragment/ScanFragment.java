package com.vnpt.vnptscanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.activity.MainActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by MinhDN on 23/11/2016.
 */

public class ScanFragment extends android.support.v4.app.Fragment{
    TextView txtNote;
    Button btnScanner;
    private ZXingScannerView mScannerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_bar_main, container, false);
        MainActivity homeActivity = (MainActivity) getActivity();
        homeActivity.getSupportActionBar().setTitle(getString(R.string.app_name));
        // change text notification
        String sourceString = "<b>" + getResources().getString(R.string.VNPTScanner) + "</b> " + " " + getResources().getString(R.string.noiDungThongBao);
        txtNote = (TextView) view.findViewById(R.id.txtNote);
        txtNote.setText(Html.fromHtml(sourceString));
        // end
        return view;
    }

}
