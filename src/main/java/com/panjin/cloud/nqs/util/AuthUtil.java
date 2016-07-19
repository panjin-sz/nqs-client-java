/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 签名生成算法工具类.
 *
 * @author panjin
 * @version $Id: AuthUtil.java 2016年7月19日 下午6:04:43 $
 */
public class AuthUtil {

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(AuthUtil.class);

    /**
     * Default encoding.
     */
    private static final String DEFAULT_ENCODING = "utf-8";

    /**
     * Hash algrithm.
     */
    private static final String HMACSHA256 = "HmacSHA256";

    /**
     * Key name for nonce.
     */
    private static final String NONCE = "nonce";

    /**
     * Key name for product key.
     */
    private static final String ACCESS_KEY = "access_key";

    public static String getSignWithParas(String accessKey, String accessSecret, Map<String, String> paras) {
        String nonce = Long.toString(System.currentTimeMillis());
        paras.put(NONCE, nonce);
        String stringToSign = getStringToSign(accessKey, paras);
        String signature = generateSignature(accessSecret, stringToSign);
        StringBuilder sb = new StringBuilder();
        sb.append(accessKey);
        sb.append("&");
        sb.append(stringToSign);
        sb.append("&");
        sb.append(signature);

        return sb.toString();
    }

    private static String getStringToSign(String productKey, Map<String, String> params) {
        Map<String, String> dataMap = new TreeMap<String, String>();
        dataMap.put(ACCESS_KEY, productKey);
        if (params != null) {
            dataMap.putAll(params);
        }
        String stringToSign = "";
        for (Map.Entry<String, String> e : dataMap.entrySet()) {
            if (!"".equals(stringToSign)) {
                stringToSign += ";";
            }
            stringToSign += e.getKey() + "=" + e.getValue();
        }
        return stringToSign;
    }

    /**
     * 认证接口签名算法.
     * 
     * @param secret
     *            accessSecret
     * @param stringToSign
     *            待签名字符串
     * @return 签名后的字符串
     */
    public static String generateSignature(String secret, String stringToSign, boolean isUrlEncoded) {
        byte[] encryptBytes = Base64.encodeBase64(hmacSha256(secret, stringToSign));
        String encryptString = new String(encryptBytes);
        if (isUrlEncoded) {
            try {
                encryptString = URLEncoder.encode(encryptString, DEFAULT_ENCODING);
                // 此处+号肯定是由空格转义而来的，因为如果本身是+的字符已经被转义成%2B了
                encryptString = encryptString.replaceAll("\\+", "%20");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
                return null;
            }
        }
        return encryptString;

    }

    /**
     * 签名生成方法.
     * 
     * @param secret
     * @param stringToSign
     * @return
     */
    public static String generateSignature(String secret, String stringToSign) {
        return generateSignature(secret, stringToSign, false);
    }

    /**
     * HmacSha256签名算法.
     * 
     * @param secret
     * @param stringToSign
     * @return
     */
    private static byte[] hmacSha256(String secret, String stringToSign) {
        try {
            byte[] secretBytes = secret.getBytes();

            SecretKeySpec signingKey = new SecretKeySpec(secretBytes, HMACSHA256);
            Mac mac = Mac.getInstance(HMACSHA256);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes(DEFAULT_ENCODING));

            return rawHmac;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
