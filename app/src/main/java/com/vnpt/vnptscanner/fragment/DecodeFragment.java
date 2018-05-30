package com.vnpt.vnptscanner.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.activity.DecodeActivity;
import com.vnpt.vnptscanner.activity.MainActivity;
import com.vnpt.vnptscanner.adapter.InformationAdapter;
import com.vnpt.vnptscanner.core.RSADecrypt;
import com.vnpt.vnptscanner.model.ResultInformation;

import java.io.InputStream;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by MinhDN on 28/11/2016.
 */

public class DecodeFragment extends android.support.v4.app.Fragment implements ZXingScannerView.ResultHandler{
    private ListView lvListResultBill;
    private ArrayList<ResultInformation> listResultDecode;
    private InformationAdapter informationAdapter;
    private ZXingScannerView mScannerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void handleResult(Result result) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.decode_fragment, container, false);
        MainActivity homeActivity = (MainActivity) getActivity();
        homeActivity.getSupportActionBar().setTitle(getString(R.string.app_name));
        //addControls(view);
        //addEvents();
        return view;
    }

    private void addEvents() {
        // decrypt Q.R code
        String resultValue = getArguments().getString("SCAN_RESULT");
        Log.e("b64_", resultValue);

        String plain = "";
        try {
            byte[] enrypted = Base64.decode(resultValue, Base64.DEFAULT);
            InputStream is = RSADecrypt.certFromResource(getActivity());
            plain = RSADecrypt.RSADecryptWithPublicKey(enrypted, is);
        } catch (Exception e) {
            DialogError();
            e.printStackTrace();
        }
        Log.e("result", plain);
        showDataDecrypt(plain);
        // end

        // capture continue scanner
//        btnContinueScanner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DecodeActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        // end
        // capture event copy text
        lvListResultBill.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                Toast.makeText(getContext(), "Đã copy dữ liệu vào pasterboard", Toast.LENGTH_SHORT).show();
                ResultInformation information = (ResultInformation) parent.getItemAtPosition(position);
                copyClipBoard(information.getLabel(), information.getValue());
                return false;
            }
        });
        // end
    }

    private void addControls(View view) {
        //tvKey = (TextView) findViewById(R.id.tvResultKey);
        listResultDecode = new ArrayList<>();
        lvListResultBill = (ListView) view.findViewById(R.id.lvListResultBill);
//        btnContinueScanner = (Button) findViewById(R.id.btnContinueScanner);
    }

    private void showDataDecrypt(String data){
        if (data != null && !data.equals("")) {
            String[] arrData = data.split(";");
            if (arrData.length >= 9) {
//                BigDecimal decimalTongTien = convertMoneyValue(arrData[2]);
//                BigDecimal decimalThue = convertMoneyValue(arrData[3]);

                // add values result in arraylist result
                String tongTien = convertMoneyValue(arrData[2]) + " VNĐ";
                String thue = convertMoneyValue(arrData[3]) + " VNĐ";
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.maHoaDon), arrData[0]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.maSoThue), arrData[1]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.tongTien), tongTien));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.thue), thue));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.ngayPhatHanh), arrData[4]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.mauSo), arrData[5]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.serialHoaDon), arrData[6]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.soHoaDon), arrData[7]));
                listResultDecode.add(new ResultInformation(getResources().getString(R.string.maCongTy), arrData[8]));
                // end
                informationAdapter = new InformationAdapter(getActivity(), R.layout.detail_bill_infomation, listResultDecode);
                lvListResultBill.setAdapter(informationAdapter);
            }
            else{
                DialogError();
            }
        }
    }

    private void copyClipBoard(String label, String values){
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, values);
        clipboard.setPrimaryClip(clip);
    }

    private String convertMoneyValue(String values) {
        if(values != null) {
            try {
                // The comma in the format specifier does the trick
                values = String.format("%,d", Long.parseLong(values.toString()));
            } catch (NumberFormatException e) {
            }
        }
        return values;
    }

    public void DialogError(){
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getResources().getString(R.string.titleDecodeError));
        b.setIcon(R.drawable.icon_warning);
        b.setMessage(getResources().getString(R.string.decodeError));
        b.setPositiveButton(Html.fromHtml("<b>" + getResources().getString(R.string.nameOk) + "</b"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(DecodeActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });

        final AlertDialog alertDialog = b.show();
        //Buttons
        Button positive_button =  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        positive_button.setGravity(Gravity.CENTER_VERTICAL);
        alertDialog.setCanceledOnTouchOutside(false);
        // b.create().show();
    }
}
