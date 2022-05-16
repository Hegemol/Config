package org.hegemol.config.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The type Md5 utils.
 */
public class Md5Utils {

    /**
     * logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Md5Utils.class);

    /**
     * Md 5 string.
     *
     * @param src     the src
     * @param charset the charset
     * @return the string
     */
    private static String md5(final String src, final String charset) {
        MessageDigest md5;
        StringBuilder hexValue = new StringBuilder(32);
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        byte[] byteArray = new byte[0];
        try {
            byteArray = src.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        byte[] md5Bytes = md5.digest(byteArray);
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * Md 5 string.
     *
     * @param src the src
     * @return the string
     */
    public static String md5(final String src) {
        return md5(src, StandardCharsets.UTF_8.name());
    }
}
