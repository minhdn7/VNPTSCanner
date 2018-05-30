package com.vnpt.vnptscanner.activity;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.Result;
import com.vnpt.vnptscanner.Manifest;
import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.fragment.DecodeFragment;
import com.vnpt.vnptscanner.fragment.ScanFragment;
import com.vnpt.vnptscanner.fragment.SettingFragment;

import io.fabric.sdk.android.Fabric;
import java.security.Policy;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ZXingScannerView.ResultHandler {
    Button btnScanner;
    TextView txtNote;
    RelativeLayout frame;
    NavigationView navigationView;
    private ZXingScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addEvents();
    }

    private void addEvents() {
        // click button scan
        btnScanner = (Button) findViewById(R.id.btnScanner);
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.noiDungThongBao), Toast.LENGTH_LONG).show();
                frame = (RelativeLayout) findViewById(R.id.frame);
                QrScanner(frame);
            }
        });
        // end
        // change text notification
        String sourceString = "<b>" + getResources().getString(R.string.VNPTScanner) + "</b> " + " " + getResources().getString(R.string.noiDungThongBao);
        txtNote = (TextView) findViewById(R.id.txtNote);
        txtNote.setText(Html.fromHtml(sourceString));
        // end
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.scanner) {
            // Handle the camera action
            FragmentManager manager = getSupportFragmentManager();
            ScanFragment scanFragment = (ScanFragment) manager.findFragmentByTag(getResources().getString(R.string.tagScanFragment));
            if (scanFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(scanFragment).commit();
            }
            //thay doi fragment
            scanFragment = new ScanFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.mainScreen, scanFragment, getResources().getString(R.string.tagScanFragment));
            fragmentTransaction.commitAllowingStateLoss();
        } else if (id == R.id.setting) {
            FragmentManager manager = getSupportFragmentManager();
            SettingFragment settingFragment = (SettingFragment) manager.findFragmentByTag(getResources().getString(R.string.tagSettingFragment));
            if (settingFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(settingFragment).commit();
            }
            //thay doi fragment
            settingFragment = new SettingFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.mainScreen, settingFragment, getResources().getString(R.string.tagSettingFragment));
            fragmentTransaction.commitAllowingStateLoss();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void QrScanner(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }else{
                mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
                setContentView(mScannerView);
                mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                mScannerView.startCamera();         // Start camera
            }
        }else{
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
            setContentView(mScannerView);
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();         // Start camera
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView != null) {
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Kết quả Scanner");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
        // end
            if(rawResult.getText() != null){
                // pass values to diffirent activity
                Intent intent = new Intent(this, DecodeActivity.class);
                intent.putExtra("SCAN_RESULT", rawResult.getText().toString());
                startActivity(intent);
                // end
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(mScannerView != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.backToExit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
                setContentView(mScannerView);
                mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                mScannerView.startCamera();         // Start camera
            } else {
                Toast.makeText(this, R.string.camera_deny, Toast.LENGTH_LONG).show();
            }

        }}//end onRequestPermissionsResult
}
