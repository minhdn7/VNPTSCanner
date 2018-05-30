package com.vnpt.vnptscanner.core;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import android.content.Context;
/**
 *
 * @author duyhoang
 */

public class RSADecrypt {
    public static InputStream certFromResource(Context c) {
//        InputStream ins = c.getResources().openRawResource(
//                c.getResources().getIdentifier("raw/cts_giaima", "raw", c.getPackageName()));
        InputStream ins = c.getResources().openRawResource(
                c.getResources().getIdentifier("raw/ct2018_decrypt_1", "raw", c.getPackageName()));
        return ins;
    }
    public static InputStream certFromResource(Context c, String dir) {
        InputStream ins = c.getResources().openRawResource(
                c.getResources().getIdentifier(dir, "raw", c.getPackageName()));
        return ins;
    }


    /**
     * Decrypt cipher from .Net code with Public Key from certificate
     * @param encryptedBytes: cipher from .Net code
     * @param certIS: InputStream of certificate
     * @return
     * @throws Exception
     */

    public static String RSADecryptWithPublicKey(byte[] encryptedBytes, InputStream certIS) throws Exception {
        X509Certificate x;

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        x = (X509Certificate) cf.generateCertificate(certIS);

        RSAPublicKey pubKey = (RSAPublicKey) x.getPublicKey();
        BigInteger modulus = pubKey.getModulus();
        BigInteger exponent = pubKey.getPublicExponent();
        reverse(encryptedBytes);
        BigInteger encData = new BigInteger(encryptedBytes);
        BigInteger bnData = encData.modPow(exponent, modulus);

        byte[] data = bnData.toByteArray();
        reverse(data);
        byte[] result = new byte[data.length - 1];
        System.arraycopy(data, 0, result, 0, result.length);
        result = RemovePadding(result);
        reverse(result);
        String re = new String(result, "UTF-8");
        return re;
    }



    private static void reverse(byte[] data) {
        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            byte temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }
    }

    private static byte[] RemovePadding(byte[] data) {
        byte[] results = new byte[data.length - 4];
        System.arraycopy(data, 0, results, 0, results.length);
        return results;
    }
}
