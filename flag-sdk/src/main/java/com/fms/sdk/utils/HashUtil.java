package com.fms.sdk.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * hash SHA-256
 *
 * @author matt.shi
 * @date 2026/07/02
 **/
public final class HashUtil {

    private HashUtil() {}

    public static int hash32(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            return ((d[0] & 0xFF) << 24)
                 | ((d[1] & 0xFF) << 16)
                 | ((d[2] & 0xFF) <<  8)
                 |  (d[3] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            return s.hashCode();
        }
    }
}