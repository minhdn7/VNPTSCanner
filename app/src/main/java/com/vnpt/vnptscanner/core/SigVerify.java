package com.vnpt.vnptscanner.core;

import android.content.Context;
import android.os.Debug;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by MinhDN on 5/12/2016.
 */

public class SigVerify{
    private static String alogrithm = "SHA1withRSA";
    public static InputStream certFromResource(Context c) {
//        InputStream ins = c.getResources().openRawResource(
//                c.getResources().getIdentifier("raw/vf", "raw", c.getPackageName()));
        InputStream ins = c.getResources().openRawResource(
                c.getResources().getIdentifier("raw/ct_2018_verify_1", "raw", c.getPackageName()));
        return ins;
    }

    public static InputStream certFromResource(Context c, String dir) {
        InputStream ins = c.getResources().openRawResource(
                c.getResources().getIdentifier(dir, "raw", c.getPackageName()));
        return ins;
    }

    public static boolean verifySignature(String data, String sig, InputStream cer) throws CertificateException {
        List<String> datas = new ArrayList<String>();
        datas.add(data);
        return verifySignature(datas, sig, cer);
    }

    public static boolean verifySignature(String[] data, String sig, InputStream cer) throws CertificateException {
        List<String> datas = new ArrayList<String>();
        for (String d : data) {
            datas.add(d);
        }
        return verifySignature(datas, sig, cer);
    }

    public static boolean verifySignature(List<String> data, String sig, InputStream cer) throws CertificateException {
        boolean ret = false;
        X509Certificate cert;
//        X509Certificate cert = Utils.StringToX509Certificate(cer);
//        if (cert == null) {
//            throw new InvalidCerException(new Date() + ":Base64 Certificate of user input incorrect");
//        }
        // 509 format convert
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) cf.generateCertificate(cer);
        // end
        try {
            PublicKey publicKey = cert.getPublicKey();

            Log.d("publicKey", publicKey.toString());
            Signature signature = Signature.getInstance(getAlogrithm());
            signature.initVerify(publicKey);
            String dataString = "";
            for (String d : data) {
                dataString += d;
            }
            byte[] data_ = dataString.getBytes();

            //byte[] sig_ = Base64Utils.base64Decode(sig);
            byte[] sig_ = Base64.decode(sig, Base64.DEFAULT);
            BufferedInputStream bufin = new BufferedInputStream(new ByteArrayInputStream(data_));

            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                signature.update(buffer, 0, len);
            }
            bufin.close();
            ret = signature.verify(sig_);

        } catch (Exception ex) {
            Logger.getLogger(SigVerify.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return ret;
    }

    public static String getAlogrithm() {
        return alogrithm;
    }

    public void setAlogrithm(String alogrithm) {
        this.alogrithm = alogrithm;
    }
}
