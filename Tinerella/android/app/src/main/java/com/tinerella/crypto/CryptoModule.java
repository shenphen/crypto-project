package com.tinerella.crypto;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.SecureRandom;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

public class CryptoModule extends ReactContextBaseJavaModule {

    public CryptoModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override //in js React.NativeModules.Crypto
    public String getName() {
        return "Crypto";
    }

    @ReactMethod
    public static void encryptAES(String message, Promise promise) {
        try {
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            String stringKey = "";

            if (secretKey != null) {
                stringKey = new String(Base64.encodeBase64(secretKey.getEncoded()));
            }

            SecureRandom random = new SecureRandom();
            byte ivBytes[] = new byte[16];
            random.nextBytes(ivBytes);

            String initVector = new String(Base64.encodeBase64(ivBytes));
            String cipher = createCipher(stringKey, initVector, message);

            WritableMap map = Arguments.createMap();
            map.putString("key", stringKey);
            map.putString("cipher", cipher);
            map.putString("iv", initVector);
    
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    private static String createCipher(String key, String initVector, String message) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(initVector.getBytes()), 0, 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(message.getBytes());
            String encryptedString = new String(Base64.encodeBase64(encrypted));
            System.out.println("encrypted string: " + encryptedString);

            return encryptedString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @ReactMethod
    public void decryptAES(String encrypted, String key, String initVector, Promise promise) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(initVector.getBytes()), 0, 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            
            WritableMap map = Arguments.createMap();
            map.putString("decrypted", new String(original));

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
            // ex.printStackTrace();
        }
    }

}