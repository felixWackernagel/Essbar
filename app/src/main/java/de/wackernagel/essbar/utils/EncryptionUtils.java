package de.wackernagel.essbar.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionUtils {

    private static final String PROVIDER = "AndroidKeyStore";
    private static final String CHARSET = "UTF-8";
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;

    public interface EncryptionCallback {
        void onEncryptionSuccess(String encryptedData, String cipherIV );

        void onUserNotAuthenticatedForEncryption();

        void onEncryptionError(Exception e );
    }

    public interface DecryptionCallback {
        void onDecryptionSuccess(String decryptedData );

        void onUserNotAuthenticatedForDecryption();

        void onDecryptionError(Exception e );
    }

    public static void encrypt( final String dataToEncrypt, final String alias, final EncryptionCallback callback ) {
        try {
            final SecretKey secretKey = getOrGenerateKey( alias );
            final Cipher cipher = Cipher.getInstance( TRANSFORMATION );
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptionIVBytes = cipher.getIV();
            byte[] dataToEncryptBytes = dataToEncrypt.getBytes(CHARSET);
            byte[] encryptedDataBytes = cipher.doFinal(dataToEncryptBytes);
            final String encryptedData = Base64.encodeToString(encryptedDataBytes, Base64.DEFAULT);
            final String encryptionIV = Base64.encodeToString(encryptionIVBytes, Base64.DEFAULT);
            callback.onEncryptionSuccess( encryptedData, encryptionIV );
        } catch( UserNotAuthenticatedException e ) {
            callback.onUserNotAuthenticatedForEncryption();
        } catch ( IOException | UnrecoverableEntryException | KeyStoreException | CertificateException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            callback.onEncryptionError( e );
        }
    }

    private static SecretKey getOrGenerateKey( final String alias ) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException, UnrecoverableEntryException {
        final KeyStore keyStore = KeyStore.getInstance( PROVIDER );
        keyStore.load( null );
        if( !keyStore.containsAlias( alias ) ) {
            return generateKey( alias );
        } else {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        }
    }

    private static SecretKey generateKey( final String alias ) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER );
        keyGenerator.init( buildKeyGeneratorSpecification( alias ) );
        return keyGenerator.generateKey();
    }

    private static KeyGenParameterSpec buildKeyGeneratorSpecification( final String alias ) {
        final int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
        return new KeyGenParameterSpec.Builder( alias, purposes )
                .setBlockModes( KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds( 5 * 60 ) // 5 minutes
                .setEncryptionPaddings( KeyProperties.ENCRYPTION_PADDING_PKCS7 )
                .build();
    }

    public static void decrypt( final String dataToDecrypt, final String alias, final String cipherIV, final DecryptionCallback callback ) {
        final byte[] encryptedDataBytes = Base64.decode( dataToDecrypt, Base64.DEFAULT );
        final byte[] cipherIVBytes = Base64.decode( cipherIV, Base64.DEFAULT );

        try {
            final KeyStore keyStore = KeyStore.getInstance( PROVIDER );
            keyStore.load( null );

            final SecretKey secretKey = (SecretKey) keyStore.getKey( alias, null );
            final Cipher cipher = Cipher.getInstance( TRANSFORMATION );
            cipher.init( Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec( cipherIVBytes ) );

            final byte[] decryptedDataBytes = cipher.doFinal( encryptedDataBytes );
            final String decryptedData = new String( decryptedDataBytes, CHARSET );

            callback.onDecryptionSuccess( decryptedData );
        } catch ( UserNotAuthenticatedException e) {
            callback.onUserNotAuthenticatedForDecryption();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            callback.onDecryptionError( e );
        }
    }
}
