package com.vnpt.vnptscanner.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.vnpt.vnptscanner.BuildConfig;
import com.vnpt.vnptscanner.R;
import com.vnpt.vnptscanner.activity.MainActivity;

import java.util.Locale;

/**
 * Created by MinhDN on 23/11/2016.
 */

public class SettingFragment extends android.support.v4.app.Fragment {
    TextView txtLanguage;
    TextView txtRate;
    TextView txtSendFeedBack;
    private String Lang = "vi";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_screen, container, false);
        MainActivity homeActivity = (MainActivity) getActivity();
        homeActivity.getSupportActionBar().setTitle(getString(R.string.caiDat));
        addControls(view);
        addEvents();
        return view;
    }

    private void addControls(View view) {
        txtLanguage = (TextView) view.findViewById(R.id.txtLanguage);
        txtRate = (TextView) view.findViewById(R.id.txtRate);
        txtSendFeedBack = (TextView) view.findViewById(R.id.txtSendFeedBack);
    }

    private void addEvents() {
        txtLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });
        txtSendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedBack();
            }
        });
    }

    private void sendFeedBack() {
        try {
            String versionName = BuildConfig.VERSION_NAME;
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getResources().getString(R.string.mailFeedBack), null));
            String subText = "Góp ý và báo lỗi VNPT Scanner phiên bản " + versionName + " hệ điều hành Android";
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subText);
            startActivity(Intent.createChooser(emailIntent, null));
        }
        catch (Exception ex){
            Log.e("Loi gui phan hoi", ex.toString());
        }

    }

    private void changeLanguage() {
        // show custom alert dialog
        LayoutInflater inflater = getLayoutInflater(null);
        final View dialoglayout = inflater.inflate(R.layout.dialog_leaguage_setting, null);
        final AlertDialog.Builder b = new AlertDialog.Builder(getContext());

        // set title
        TextView title = new TextView(getContext());
        title.setText(getResources().getString(R.string.titleChonNgonNgu));
        title.setPadding(10, 10, 20, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(18);
        // end
        // get controls in alert dialog
        TextView txtTiengAnh = (TextView) dialoglayout.findViewById(R.id.txtTiengAnh);
        TextView txtTiengViet = (TextView) dialoglayout.findViewById(R.id.txtTiengViet);
        TextView txtHuy = (TextView) dialoglayout.findViewById(R.id.txtHuy);
        // end
        b.setCustomTitle(title);
        b.setView(dialoglayout);
        final AlertDialog alertDialog = b.show();
        // get events
        txtTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLang("en");
                alertDialog.dismiss();
            }
        });
        txtTiengViet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLang("vi");
                alertDialog.dismiss();
            }
        });
        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        // end
    }

    private void rateApp() {
        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void changeLang(String leng){
        Locale locale = new Locale(leng);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getApplicationContext().getResources().updateConfiguration(config, null);
        restartFragent();
        restartNavigationBar();
    }

    private void restartActivity() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

    private void restartFragent() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        SettingFragment settingFragment = (SettingFragment) manager.findFragmentByTag(getResources().getString(R.string.tagSettingFragment));
        if (settingFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(settingFragment).commit();
        }
        //thay doi fragment
        settingFragment = new SettingFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.mainScreen, settingFragment, getResources().getString(R.string.tagSettingFragment));
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void restartNavigationBar(){
//        TextView scanner = (TextView) getActivity().getWindow().getDecorView().findViewById(R.id.scanner);
//        TextView setting = (TextView) getActivity().getWindow().getDecorView().findViewById(R.id.setting);
//        scanner.setText(getString(R.string.quetHoaDonDienTu));
//        setting.setText(getString(R.string.caiDat));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }
}
