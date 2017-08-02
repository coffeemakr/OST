package ch.unstable.ost.error;


import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ErrorUtils {
    private final String TAG = "ErrorUtils";

    private ErrorUtils() {}

    /*
    * Get the X.509 certificate.
    */
    private static X509Certificate getCertificateFromSignature(Signature signature) throws CertificateException {
        final byte[] rawCert = signature.toByteArray();
        InputStream certStream = new ByteArrayInputStream(rawCert);
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        return (X509Certificate) certFactory.generateCertificate(certStream);
    }

    public static byte[] getSHA256Fingerprint(X509Certificate certificate) throws CertificateEncodingException {
        try {
            return getFingerprint(certificate, "SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getFingerprint(X509Certificate cert, String algorithm)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] der = cert.getEncoded();
        md.update(der);
        return md.digest();

    }

    public static String printableSignature(Signature signature) {
        X509Certificate certificate;
        try {
            certificate = getCertificateFromSignature(signature);
        } catch (Exception e) {
            return signature.toCharsString();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Subject: ");
        builder.append(certificate.getSubjectDN().getName());
        builder.append('\n');
        builder.append("Issuer: ");
        builder.append(certificate.getIssuerDN().getName());
        builder.append('\n');
        builder.append("Serial Number: ");
        builder.append(certificate.getSerialNumber());
        builder.append('\n');
        builder.append("Not After: ");
        builder.append(certificate.getNotAfter());
        builder.append('\n');
        builder.append("Not Before: ");
        builder.append(certificate.getNotBefore());
        builder.append('\n');
        return builder.toString();
    }
}
