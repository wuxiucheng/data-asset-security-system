package com.dataasset.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * 脱敏算法工具类
 */
@Slf4j
@Component
public class MaskAlgorithmUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 掩码脱敏
     * @param value 原始值
     * @param start 开始位置
     * @param end 结束位置
     * @param maskChar 掩码字符
     * @return 脱敏后的值
     */
    public static String mask(String value, int start, int end, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        int length = value.length();
        if (start < 0) start = 0;
        if (end > length) end = length;
        if (start >= end) return value;
        
        char[] chars = value.toCharArray();
        for (int i = start; i < end; i++) {
            chars[i] = maskChar;
        }
        return new String(chars);
    }

    /**
     * 手机号掩码 (保留前3后4)
     */
    public static String maskPhone(String phone) {
        return mask(phone, 3, 7, '*');
    }

    /**
     * 身份证号掩码 (保留前6后4)
     */
    public static String maskIdCard(String idCard) {
        return mask(idCard, 6, idCard.length() - 4, '*');
    }

    /**
     * 邮箱掩码 (保留前3和@后的域名)
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        return mask(email, 3, atIndex, '*');
    }

    /**
     * 银行卡号掩码 (保留前4后4)
     */
    public static String maskBankCard(String bankCard) {
        return mask(bankCard, 4, bankCard.length() - 4, '*');
    }

    /**
     * 姓名掩码 (保留姓)
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        return name.charAt(0) + "***";
    }

    /**
     * 替换脱敏
     * @param value 原始值
     * @param replacement 替换值
     * @return 替换后的值
     */
    public static String replace(String value, String replacement) {
        return replacement;
    }

    /**
     * 哈希脱敏
     * @param value 原始值
     * @param algorithm 哈希算法 (MD5/SHA256)
     * @return 哈希后的值
     */
    public static String hash(String value, String algorithm) {
        if (value == null) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("哈希脱敏失败", e);
            return value;
        }
    }

    /**
     * MD5哈希
     */
    public static String hashMD5(String value) {
        return hash(value, "MD5");
    }

    /**
     * SHA256哈希
     */
    public static String hashSHA256(String value) {
        return hash(value, "SHA-256");
    }

    /**
     * 截断脱敏
     * @param value 原始值
     * @param length 保留长度
     * @return 截断后的值
     */
    public static String truncate(String value, int length) {
        if (value == null || value.length() <= length) {
            return value;
        }
        return value.substring(0, length) + "...";
    }

    /**
     * 打乱脱敏
     * @param value 原始值
     * @return 打乱后的值
     */
    public static String shuffle(String value) {
        if (value == null || value.length() <= 1) {
            return value;
        }
        
        char[] chars = value.toCharArray();
        List<Character> list = new ArrayList<>();
        for (char c : chars) {
            list.add(c);
        }
        Collections.shuffle(list, RANDOM);
        
        StringBuilder sb = new StringBuilder();
        for (Character c : list) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 数值范围脱敏
     * @param value 原始数值
     * @param range 范围大小
     * @return 脱敏后的范围值
     */
    public static String range(Number value, int range) {
        if (value == null) {
            return null;
        }
        
        long num = value.longValue();
        long lower = (num / range) * range;
        long upper = lower + range - 1;
        return lower + "-" + upper;
    }

    /**
     * 泛化脱敏 (将具体值替换为泛化值)
     * @param value 原始值
     * @param rules 泛化规则 (Map<具体值, 泛化值>)
     * @return 泛化后的值
     */
    public static String generalize(String value, Map<String, String> rules) {
        if (value == null || rules == null) {
            return value;
        }
        return rules.getOrDefault(value, value);
    }
}
