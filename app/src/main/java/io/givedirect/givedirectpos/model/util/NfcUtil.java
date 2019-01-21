package io.givedirect.givedirectpos.model.util;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import org.stellar.sdk.KeyPair;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import timber.log.Timber;

public class NfcUtil {
    private static final int PRA_ID_LENGTH = 5;
    private static Cipher decryptCipher;
    private static Cipher encryptCipher;
    private static String lastDecryptKey;
    private static String lastEncryptKey;

    /**
     * Parses the {@link GiveDirectNFCWallet} out of an intent if possible. If the intent is not
     * an NFC/Ndef related intent, {@code null} will be returned.
     *
     * @param intent   The intent to parse.
     * @param authSeed Key to decrypt encrypted private key.
     * @return An {@link GiveDirectNFCWallet} parsed from the provided intent; null if intent was
     * not NFC/Ndef related.
     */
    @Nullable
    public static GiveDirectNFCWallet parseNfcIntent(@Nullable Intent intent,
                                                     @NonNull String authSeed) {
        if (intent == null || !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Timber.d("Intent not NFC (Ndef) related");
            return null;
        }

        try {
            GiveDirectNFCWallet giveDirectNFCWallet = new GiveDirectNFCWallet();

            // Always expect the first entry to be the encrypted secret key.
            String encodedSeed = getNdefRecordPayload(Observable
                    .fromArray(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))
                    .cast(NdefMessage.class)
                    .blockingFirst()
                    .getRecords()[0]);

            giveDirectNFCWallet.privateKey = decryptSeed(encodedSeed, authSeed);

            if (!AccountUtil.secretSeedOfProperLength(giveDirectNFCWallet.privateKey)
                    || !AccountUtil.secretSeedOfProperPrefix(giveDirectNFCWallet.privateKey)
                    || !AccountUtil.secretSeedCanBeDecoded(giveDirectNFCWallet.privateKey)) {
                Timber.e("Private key invalid: %s", giveDirectNFCWallet.privateKey);
                giveDirectNFCWallet.isValidFormat = false;
                return giveDirectNFCWallet;
            } else {
                giveDirectNFCWallet.publicKey =
                        KeyPair.fromSecretSeed(giveDirectNFCWallet.privateKey).getAccountId();
                giveDirectNFCWallet.praID = getPraId(giveDirectNFCWallet.publicKey);
                giveDirectNFCWallet.isValidFormat = true;
            }

            return giveDirectNFCWallet;
        } catch (Exception e) {
            Timber.e(e, "Failed to parse NFC for GiveDirect wallet payload");
            return new GiveDirectNFCWallet();
        }
    }

    @Nullable
    private static String getNdefRecordPayload(@NonNull NdefRecord ndefRecord) throws UnsupportedEncodingException {
        if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
            if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                byte[] payloadBytes = ndefRecord.getPayload();
                boolean isUTF8 = (payloadBytes[0] & 0x080) == 0;  //status byte: bit 7 indicates encoding (0 = UTF-8, 1 = UTF-16)
                int languageLength = payloadBytes[0] & 0x03F;     //status byte: bits 5..0 indicate length of language code
                int textLength = payloadBytes.length - 1 - languageLength;
                return new String(payloadBytes, 1 + languageLength, textLength, isUTF8 ? "UTF-8" : "UTF-16");
            }
        }

        return "";
    }

    public static String getPraId(@NonNull String publicKey) {
        return publicKey.substring(publicKey.length() - PRA_ID_LENGTH, publicKey.length());
    }

    public static String decryptSeed(String encryptedSeed, String key) throws Exception {
        byte[] encryptedBytes = Base64.decode(encryptedSeed, Base64.DEFAULT);

        if (decryptCipher == null || !key.equals(lastDecryptKey)) {
            lastDecryptKey = key;
            decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] staticKey = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(staticKey, "AES");
            byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        }

        return new String(decryptCipher.doFinal(encryptedBytes));
    }

    public static String encryptSeed(String decryptedSeed, String key) throws Exception {
        if (encryptCipher == null || !key.equals(lastEncryptKey)) {
            lastEncryptKey = key;
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] staticKey = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(staticKey, "AES");
            byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        }

        return Base64.encodeToString(encryptCipher.doFinal(decryptedSeed.getBytes("UTF-8")), Base64.NO_WRAP);
    }

    public static boolean bindNFCMessage(@NonNull String encryptedSeed, @NonNull Intent intent) {
        try {
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = encryptedSeed.getBytes("UTF-8"); // Content in UTF-8
            int langSize = lang.length;
            int textLength = text.length;
            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                    new byte[]{0}, payload.toByteArray());
            NdefMessage message = new NdefMessage(new NdefRecord[]{ndefRecord});
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            return writeMessageToTag(message, tag);
        } catch (Exception e) {
            Timber.e(e, "Failed to write NdefMessage");
            return false;
        }
    }

    private static boolean writeMessageToTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                ndef.connect();
                if (ndef.getMaxSize() < ndefMessage.toByteArray().length) {
                    return false;
                }

                if (ndef.isWritable()) {
                    ndef.writeNdefMessage(ndefMessage);
                    ndef.close();
                    return true;
                } else {
                    // NFC not writable
                    ndef.close();
                    return false;
                }
            }

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable != null) {
                ndefFormatable.connect();
                ndefFormatable.format(ndefMessage);
                ndefFormatable.close();
                return true;
            } else {
                // NDEF not supported
                return false;
            }

        } catch(Exception e){
            Timber.e("Failed to write NdefMessage to Tag");
            return false;
        }
    }



    public static class GiveDirectNFCWallet implements Parcelable {
        private boolean isValidFormat = false;
        private String praID;
        private String publicKey;
        private String privateKey;

        public GiveDirectNFCWallet() {
        }

        protected GiveDirectNFCWallet(Parcel in) {
            isValidFormat = in.readByte() != 0;
            praID = in.readString();
            publicKey = in.readString();
            privateKey = in.readString();
        }

        public static final Creator<GiveDirectNFCWallet> CREATOR = new Creator<GiveDirectNFCWallet>() {
            @Override
            public GiveDirectNFCWallet createFromParcel(Parcel in) {
                return new GiveDirectNFCWallet(in);
            }

            @Override
            public GiveDirectNFCWallet[] newArray(int size) {
                return new GiveDirectNFCWallet[size];
            }
        };

        public boolean isValidFormat() {
            return isValidFormat;
        }

        public String getPraID() {
            return praID;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (isValidFormat ? 1 : 0));
            dest.writeString(praID);
            dest.writeString(publicKey);
            dest.writeString(privateKey);
        }
    }
}
