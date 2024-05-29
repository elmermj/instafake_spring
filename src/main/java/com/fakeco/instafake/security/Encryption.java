package com.fakeco.instafake.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Encryption {

    public static List<Map<String, String>> mappedDictionary = Arrays.<Map<String, String>>asList(
            Map.of("a", "88"),
            Map.of("b", "81"),
            Map.of("c", "82"),
            Map.of("d", "83"),
            Map.of("e", "84"),
            Map.of("f", "85"),
            Map.of("g", "86"),
            Map.of("h", "18"),
            Map.of("i", "11"),
            Map.of("j", "12"),
            Map.of("k", "13"),
            Map.of("l", "14"),
            Map.of("m", "15"),
            Map.of("n", "16"),
            Map.of("o", "28"),
            Map.of("p", "21"),
            Map.of("q", "22"),
            Map.of("r", "23"),
            Map.of("s", "24"),
            Map.of("t", "25"),
            Map.of("u", "26"),
            Map.of("v", "38"),
            Map.of("w", "31"),
            Map.of("x", "32"),
            Map.of("y", "33"),
            Map.of("z", "34"),
            Map.of("A", "35"),
            Map.of("B", "36"),
            Map.of("C", "48"),
            Map.of("D", "41"),
            Map.of("E", "42"),
            Map.of("F", "43"),
            Map.of("G", "44"),
            Map.of("H", "45"),
            Map.of("I", "46"),
            Map.of("J", "58"),
            Map.of("K", "51"),
            Map.of("L", "52"),
            Map.of("M", "53"),
            Map.of("N", "54"),
            Map.of("O", "55"),
            Map.of("P", "56"),
            Map.of("Q", "68"),
            Map.of("R", "61"),
            Map.of("S", "62"),
            Map.of("T", "63"),
            Map.of("U", "64"),
            Map.of("V", "65"),
            Map.of("W", "66"),
            Map.of("X", "78"),
            Map.of("Y", "71"),
            Map.of("Z", "72"),
            Map.of("1", "73"),
            Map.of("2", "74"),
            Map.of("3", "75"),
            Map.of("4", "76"),
            Map.of("5", "99"),
            Map.of("6", "91"),
            Map.of("7", "92"),
            Map.of("8", "93"),
            Map.of("9", "94"),
            Map.of("0", "95"),
            Map.of("=", "96"),
            Map.of("-", "90")
    );

    public static String generateSalt() {
        // Get the current date and time as a string
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Convert the date-time string to a list of characters
        List<Character> letterSplit = dateTime.chars().mapToObj(e -> (char) e).collect(Collectors.toList());

        // Shuffle the list of characters using a secure random instance
        Collections.shuffle(letterSplit, new SecureRandom());

        // Filter out only alphanumeric characters and collect them into a list
        List<Character> alphanumeric = letterSplit.stream()
                .filter(Character::isLetterOrDigit)
                .toList();

        // Join the characters into a single string
        StringBuilder salt = new StringBuilder();
        for (Character ch : alphanumeric) {
            salt.append(ch);
        }

        return salt.toString();
    }

    public static byte[] deriveKey(String password, String salt) throws Exception {
        byte[] saltBytes = salt.getBytes("UTF-8");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 1000, 32);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return Arrays.copyOf(keyBytes, 32);
    }

    public static byte[] encrypt(String plaintext, byte[] key) throws Exception {
        // Initialize cipher for encryption with AES/CBC/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]); // Initialization vector
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);

        // Convert plaintext to bytes and pad if necessary
        byte[] textBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        int blockSize = cipher.getBlockSize();
        int paddingLength = blockSize - (textBytes.length % blockSize);
        byte[] paddedTextBytes = Arrays.copyOf(textBytes, textBytes.length + paddingLength);
        for (int i = textBytes.length; i < paddedTextBytes.length; i++) {
            paddedTextBytes[i] = (byte) paddingLength; // PKCS7 padding
        }

        // Encrypt the padded plaintext
        return cipher.doFinal(paddedTextBytes);
    }

    public static String divideAndShuffle(String cipherText, String saltCode) {
        SecureRandom random = new SecureRandom();

        int cipherLen = cipherText.length();
        int saltLen = saltCode.length();

        int randomNumberCipher = random.nextInt(cipherLen - 5);
        int randomNumberSalt = random.nextInt(saltLen - 5);

        String cipherText1 = cipherText.substring(0, randomNumberCipher);
        String cipherText2 = cipherText.substring(randomNumberCipher);
        String saltCode1 = saltCode.substring(0, randomNumberSalt);
        String saltCode2 = saltCode.substring(randomNumberSalt);

        String result;
        if (randomNumberCipher > randomNumberSalt) {
            result = String.format("%02d%02d%s%s%s%s%02d%02d",
                    cipherLen - randomNumberCipher, randomNumberCipher,
                    cipherText1, saltCode1, cipherText2, saltCode2,
                    randomNumberSalt, saltLen - randomNumberSalt);
        } else if (randomNumberCipher < randomNumberSalt) {
            result = String.format("%02d%02d%s%s%s%s%02d%02d",
                    cipherLen - randomNumberCipher, randomNumberCipher,
                    saltCode1, cipherText2, saltCode2, cipherText1,
                    randomNumberSalt, saltLen - randomNumberSalt);
        } else {
            result = String.format("%02d%02d%s%s%s%s%02d%02d",
                    cipherLen - randomNumberCipher, randomNumberCipher,
                    cipherText2, saltCode1, cipherText1, saltCode2,
                    randomNumberSalt, saltLen - randomNumberSalt);
        }

        return result;
    }

    public static String base7Encoder(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            String charStr = Character.toString(text.charAt(i));
            String convertedChar = "";

            for (Map<String, String> mapping : mappedDictionary) {
                if (mapping.containsKey(charStr)) {
                    convertedChar = mapping.get(charStr);
                    break;
                }
            }

            result.append(convertedChar);
        }

        return result.toString();
    }

    public static String execute(String username, String password) throws Exception {
        String salt = generateSalt();
        System.out.println("SALT ::: " + salt);

        byte[] key = deriveKey(username, salt);
        System.out.println("KEY  ::: " + Base64.getUrlEncoder().encodeToString(key));

        byte[] encryptedPIN = encrypt(password, key);

        String encryptedPINString = Base64.getUrlEncoder().encodeToString(encryptedPIN);
        String stageTwoEncryptedPIN = divideAndShuffle(encryptedPINString, salt);
        String base7EncodedPIN = base7Encoder(stageTwoEncryptedPIN);

        System.out.println("base7EncodedPIN ::: " + base7EncodedPIN);

        return base7EncodedPIN;
    }
}
