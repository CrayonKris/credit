package com.bonc.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

/**
 * AES加解密类
 * @author xupengfei@chinasofti.com
 * @date 2017/3/31 14:20
 */
public class AesEncrypt {
public final static String UTF_8="UTF-8";

    /**
     * 加密方法
     * @param sSrc 被加密内容
     * @param sKey 加密key
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            return null;
        }
       
        if (sKey.length() != 16) {
            throw   new InvalidKeyException("The key's length must be 16");
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("Xadiapdfaxi0s91D".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(UTF_8));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 解密方法
     * @param sSrc 被解密内容
     * @param sKey 解密key
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey) throws Exception {
        try {
            
            if (sKey == null) {
                return null;
            }
            
            if (sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("Xadiapdfaxi0s91D"
                    .getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new Base64().decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original,UTF_8);
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }
}

