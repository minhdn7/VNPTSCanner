package com.vnpt.vnptscanner.activity;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.adapter.InformationAdapter;
import com.vnpt.vnptscanner.core.RSADecrypt;
import com.vnpt.vnptscanner.core.SigVerify;
import com.vnpt.vnptscanner.model.ResultInformation;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DecodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ListView lvListResultBill;
    private ArrayList<ResultInformation> listResultDecode;
    private InformationAdapter informationAdapter;
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        // set custom action bar
        customActionBar();
        addControls();
        addEvents();
    }

    private void customActionBar() {
        //hiding default app icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_left);
    }

    private void addEvents() {
        // decrypt Q.R code
        String resultValue = getIntent().getStringExtra("SCAN_RESULT");
        Log.e("b64_", resultValue);
        // spilit result value
        String[] arrDataResult = resultValue.split(";");
        String plain = "";
        Boolean isVerify = false;
        try {
            String olderVerifyDir = "raw/vf";
//            String olderVerifyDir = "raw/cts";
            String newVerifyDir = "raw/ct_2018_verify_1";

            String olderDeCryptDir = "raw/cts_giaima";
            String newDeCryptDir = "raw/ct2018_decrypt_1";

            byte[] enrypted = Base64.decode(arrDataResult[1], Base64.DEFAULT);

            for(int i = 0; i < 2; i++){
                if(i == 0){
                    InputStream is = RSADecrypt.certFromResource(this, newDeCryptDir);
                    InputStream certVerify = SigVerify.certFromResource(this, newVerifyDir);
                    // decrypt string infomation with cert new
                    plain = RSADecrypt.RSADecryptWithPublicKey(enrypted, is);
                    // verify with cert new
                    Log.d("string 1", plain);
                    isVerify = SigVerify.verifySignature(plain, arrDataResult[0], certVerify);
                    if(isVerify) break;
                }else{
                    enrypted = Base64.decode(arrDataResult[1], Base64.DEFAULT);
                    InputStream isDecrypt2 = RSADecrypt.certFromResource(this, olderDeCryptDir);
                    InputStream certVerify2 = SigVerify.certFromResource(this, olderVerifyDir);
                    // decrypt string infomation with cert older
                    plain = RSADecrypt.RSADecryptWithPublicKey(enrypted, isDecrypt2);
                    // verify with cert older
                    Log.d("string 2", plain);
                    isVerify = SigVerify.verifySignature(plain, arrDataResult[0], certVerify2);

                }
            }


            // end
        } catch (Exception e) {
            DialogError();
            e.printStackTrace();
        }
        Log.e("result", plain);
        if(isVerify) {
            showDataDecrypt(plain);
        }
        else{
            Log.d("VERIFY FALSE", "qua trinh verify loi");
            DialogError();
        }
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
                Toast.makeText(DecodeActivity.this, "Đã copy dữ liệu vào pasterboard", Toast.LENGTH_SHORT).show();
                ResultInformation information = (ResultInformation) parent.getItemAtPosition(position);
                copyClipBoard(information.getLabel(), information.getValue());
                return false;
            }
        });
        // end
    }

    private void addControls() {
        //tvKey = (TextView) findViewById(R.id.tvResultKey);
        listResultDecode = new ArrayList<>();
        lvListResultBill = (ListView) findViewById(R.id.lvListResultBill);
//        btnContinueScanner = (Button) findViewById(R.id.btnContinueScanner);
    }

    private void showDataDecrypt(String data){
        if (data != null && !data.equals("")) {
            String[] arrData = data.split(";");
            if (arrData.length >= 9) {
//                BigDecimal decimalTongTien = convertMoneyValue(arrData[2]);
//                BigDecimal decimalThue = convertMoneyValue(arrData[3]);

                // add values result in arraylist result
                String tongTien = convertMoneyValue(arrData[2]);
                String thue = convertMoneyValue(arrData[3]);
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
                informationAdapter = new InformationAdapter(this, R.layout.detail_bill_infomation, listResultDecode);
                lvListResultBill.setAdapter(informationAdapter);
            }
            else{
                DialogError();
            }
        }
    }

    private void copyClipBoard(String label, String values){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, values);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_continue_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scanQrCode:
                if(mScannerView == null) {
                    QrScanner();
                }
                break;
            case android.R.id.home:
                Intent intent = new Intent(DecodeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
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

    @Override
    public void handleResult(Result rawResult) {
        if(rawResult.getText() != null){
            Intent intent = new Intent(this, DecodeActivity.class);
            intent.putExtra("SCAN_RESULT", rawResult.getText().toString());
            startActivity(intent);
        }
        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView != null) {
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }

    public void QrScanner(){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);        // Register ourselves as a handler for scan results.
        mScannerView.startCamera();                 // Start camera
    }

    public void DialogError(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getResources().getString(R.string.titleDecodeError));
        b.setIcon(R.drawable.icon_warning);
        b.setMessage(getResources().getString(R.string.decodeError));
        b.setPositiveButton(Html.fromHtml("<b>" + getResources().getString(R.string.nameOk) + "</b"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DecodeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final AlertDialog alertDialog = b.show();
        //Buttons
        Button positive_button =  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f);
//        positive_button.setLayoutParams(params);
        positive_button.setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
    }
}
