package io.givedirect.givedirectpos.model.util;

import android.support.annotation.NonNull;
import android.util.Base64;

import org.stellar.sdk.Transaction;
import org.stellar.sdk.xdr.TransactionEnvelope;
import org.stellar.sdk.xdr.XdrDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionUtil {
    public static String getEnvelopeXDRBase64(@NonNull Transaction transaction) throws IOException {
        TransactionEnvelope envelope = transaction.toEnvelopeXdr();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XdrDataOutputStream xdrOutputStream = new XdrDataOutputStream(outputStream);
        TransactionEnvelope.encode(xdrOutputStream, envelope);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
    }
}
