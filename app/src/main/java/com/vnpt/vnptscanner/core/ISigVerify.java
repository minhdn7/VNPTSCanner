package com.vnpt.vnptscanner.core;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * Created by MinhDN on 5/12/2016.
 */

public interface ISigVerify {
    public boolean verifySignature(String data, String sig, InputStream cer) throws CertificateException;
    public boolean verifySignature(String[] data, String sig, InputStream cer) throws CertificateException;
    public boolean verifySignature(List<String> data, String sig, InputStream cer) throws CertificateException;
}
