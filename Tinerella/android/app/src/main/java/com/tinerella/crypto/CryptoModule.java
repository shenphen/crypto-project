package com.tinerella.crypto;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
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

    @Override
    public String getName() {
        return "Crypto";
    }

    @ReactMethod
    public static void encryptAES(String message, Promise promise) {
        try {
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            BigInteger intKey = new BigInteger("0");

            if (secretKey != null) {
                intKey = new BigInteger(secretKey.getEncoded());
            }

            byte ivBytes[] = new byte[16];
            String initVector = new String(Base64.encodeBase64(ivBytes));
            String cipher = createCipher(intKey, initVector, message);

            WritableMap map = Arguments.createMap();
            map.putString("cipher", cipher);
            map.putString("key", intKey.toString());
            map.putString("iv", initVector);
    
            promise.resolve(map);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    private static String createCipher(BigInteger key, String initVector, String message) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(initVector.getBytes()), 0, 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key.toByteArray(), "AES");

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
    public void decryptGarbled(ReadableArray garbled, String key, Promise promise) {
        byte ivBytes[] = new byte[16];
        String initVector = new String(Base64.encodeBase64(ivBytes));
        IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(initVector.getBytes()), 0, 16);
        BigInteger keyInt = new BigInteger(key);
        SecretKeySpec skeySpec = new SecretKeySpec(keyInt.toByteArray(), "AES");

        
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            String encrypted = garbled.getString(0);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            
            WritableMap map = Arguments.createMap();
            map.putString("decrypted", new String(original));

            promise.resolve(map);
        } catch (Exception ex) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                String encrypted = garbled.getString(1);
    
                byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
                
                WritableMap map = Arguments.createMap();
                map.putString("decrypted", new String(original));
    
                promise.resolve(map);
            } catch (Exception e) {
                promise.reject(e);
                // ex.printStackTrace();
            }
        }
    }

    @ReactMethod
    public void generateGarbled(Boolean choice, Promise promise) {
        try {
            SecretKey keyYes = KeyGenerator.getInstance("AES").generateKey();
            SecretKey keyNo = KeyGenerator.getInstance("AES").generateKey();
            BigInteger intYesKey = new BigInteger("0");
            BigInteger intNoKey = new BigInteger("0");

            if (keyYes != null) {
                intYesKey = new BigInteger(keyYes.getEncoded()).abs();
            }
            if (keyNo != null) {
                intNoKey = new BigInteger(keyNo.getEncoded()).abs();
            }

            byte ivBytes[] = new byte[16];
            String initVector = new String(Base64.encodeBase64(ivBytes));

            String cipherYes = createCipher(intYesKey, initVector, (choice) ? "Yes" : "Noo");
            String cipherNo = createCipher(intNoKey, initVector, "Noo");

            
            WritableMap map = Arguments.createMap();
            Random rand = new Random();
            Boolean order = rand.nextBoolean();
            BigInteger c = random();

            map.putString("c", c.toString());
            map.putString("keyYes", intYesKey.toString());
            map.putString("keyNo", intNoKey.toString());
            map.putString("cipher0", order ? cipherYes : cipherNo);
            map.putString("cipher1", order ? cipherNo : cipherYes);

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void generatePublicKeys(String cStr, boolean choice, Promise promise) {
        try {
            BigInteger c = new BigInteger(cStr);
            BigInteger k = random();
            BigInteger masked = g.modPow(k, p);
            BigInteger faked = masked.modInverse(p).multiply(c).mod(p);

            BigInteger pk0 = choice ? faked : masked;
            BigInteger pk1 = choice ? masked : faked;

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

            int value = c.compareTo((pk0.multiply(pk1)).mod(p));
            
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
            BigInteger x0  = new BigInteger(key0);
            BigInteger x1  = new BigInteger(key1);

            BigInteger c0v1 = g.modPow(r0, p);
            BigInteger c0v2 = (BigInteger.valueOf(pk0.modPow(r0, p).hashCode()).xor(x0));
            BigInteger c1v1 = g.modPow(r1, p);
            BigInteger c1v2 = (BigInteger.valueOf(pk1.modPow(r1, p).hashCode()).xor(x1));

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

            BigInteger xb = BigInteger.valueOf(v1.modPow(k, p).hashCode()).xor(v2);

            String key = xb.mod(p).toString();

            WritableMap map = Arguments.createMap();
            map.putString("key", key);

            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    private static BigInteger random() {
        return new BigInteger(1024, new Random());
    }
}