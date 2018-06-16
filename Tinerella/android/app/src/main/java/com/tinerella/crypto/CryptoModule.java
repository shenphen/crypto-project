package com.tinerella.crypto;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
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
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;

public class CryptoModule extends ReactContextBaseJavaModule {

    static private BigInteger p = new BigInteger("25195908475657893494027183240048398571429282126204032027777137836043662020707595556264018525880784406918290641249515082189298559149176184502808489120072844992687392807287776735971418347270261896375014971824691165077613379859095700097330459748808428401797429100642458691817195118746121515172654632282216869987549182422433637259085141865462043576798423387184774447920739934236584823824281198163815010674810451660377306056201619676256133844143603833904414952634432190114657544454178424020924616515723350778707749817125772467962926386356373289912154831438167899885040445364023527381951378636564391212010397122822120720357");
    static private BigInteger g = new BigInteger("1094829921532959383743549924496915544028325770356823720447883");

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
            map.putString("cipher", cipher);
            map.putString("key", stringKey);
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

    @ReactMethod
    public void generateC(Promise promise) {
        try {
            BigInteger c = random();

            WritableMap map = Arguments.createMap();
            map.putString("c", c.toString());
            
            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void generatePublicKeys(String cStr, boolean choice, Promise promise) {
        try {
            BigInteger c = new BigInteger(cStr);
            BigInteger pk0 = new BigInteger("0");
            BigInteger pk1 = new BigInteger("0");
            BigInteger k = random();

            if(choice) {
                pk1 = g.modPow(k, p);
                pk0 = pk1.modInverse(p).multiply(c).mod(p);
            } else {
                pk0 = g.modPow(k, p);
                pk1 = pk0.modInverse(p).multiply(c).mod(p);
            }

            WritableMap map = Arguments.createMap();
            map.putString("pk0", pk0.toString());
            map.putString("pk1", pk1.toString());
            map.putString("k", k.toString());

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void checkPublicKeys(String cStr, String pk0Str, String pk1Str, Promise promise) {
        try {
            BigInteger c = new BigInteger(cStr);
            BigInteger pk0 = new BigInteger(pk0Str);
            BigInteger pk1 = new BigInteger(pk1Str);

            int value = c.compareTo(pk0.multiply(pk1).mod(p));
            
            boolean result = false;
            if(value == 0) {
                result = true;
            }

            WritableMap map = Arguments.createMap();
            map.putBoolean("result", result);

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void encryptElGamal(String pk0Str, String pk1Str, String key0, String key1, Promise promise) {
        try {
            BigInteger r0 = random();
            BigInteger r1 = random();
            BigInteger pk0 = new BigInteger(pk0Str);
            BigInteger pk1 = new BigInteger(pk1Str);
            BigInteger x0  = Base64.decodeInteger(key0.getBytes("UTF-8")).mod(p);
            BigInteger x1  = Base64.decodeInteger(key1.getBytes("UTF-8")).mod(p);

            BigInteger c0v1 = g.modPow(r0, p);
            BigInteger c0v2 = hash(pk0, r0).xor(x0);
            BigInteger c1v1 = g.modPow(r1, p);
            BigInteger c1v2 = hash(pk1, r1).xor(x1);

            WritableMap c0 = Arguments.createMap();
            c0.putString("v1", c0v1.toString());
            c0.putString("v2", c0v2.toString());
            WritableMap c1 = Arguments.createMap();
            c1.putString("v1", c1v1.toString());
            c1.putString("v2", c1v2.toString());

            WritableMap map = Arguments.createMap();
            map.putMap("c0", c0);
            map.putMap("c1", c1);

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void decryptElGamal(ReadableMap cb, String kStr, Promise promise) {
        try {
            BigInteger k = new BigInteger(kStr);
            BigInteger v1 = new BigInteger(cb.getString("v1"));
            BigInteger v2 = new BigInteger(cb.getString("v2"));

            BigInteger xb = hash(v1, k).xor(v2).mod(p);

            String key = new String(Base64.encodeBase64(Base64.encodeInteger(xb)));

            WritableMap map = Arguments.createMap();
            map.putString("key", key);

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    private static BigInteger hash(BigInteger base, BigInteger exponent) {
        return base.flipBit(base.bitLength()).modPow(exponent, p);
    }

    private static BigInteger random() {
        return new BigInteger(2048, new Random());
    }
}