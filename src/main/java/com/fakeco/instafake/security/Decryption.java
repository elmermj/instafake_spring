package com.fakeco.instafake.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.*;

import static java.util.Base64.getUrlDecoder;

public class Decryption {

    private static final List<Map<String, String>> mappedDictionary = Arrays.asList(
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
    public static String decrypt(String cipherText, String password, String salt) throws Exception {
        // Base64 decode the encrypted text
        byte[] encryptedBytes = getUrlDecoder().decode(cipherText);

        int iterationCount = 10000;
        int keyLength = 32;
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterationCount, keyLength * 8);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Decrypt the text
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]); // since it used Uint8List(16) in Dart
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String[] decryptGateway(String stageTwoEncrypted) throws Exception {
        int length = stageTwoEncrypted.length();
        int cipherLen = Integer.parseInt(stageTwoEncrypted.substring(0, 2));
        int cipherRand = Integer.parseInt(stageTwoEncrypted.substring(2, 4));
        int saltRand = Integer.parseInt(stageTwoEncrypted.substring(length - 4, length - 2));
        int saltLen = Integer.parseInt(stageTwoEncrypted.substring(length - 2));

        String filtered = stageTwoEncrypted.substring(4, length - 4);

        String cipherText1, cipherText2, saltCode1, saltCode2;

        if (cipherRand > saltRand) {
            cipherText1 = filtered.substring(0, cipherRand);
            saltCode1 = filtered.substring(cipherRand, cipherRand + saltRand);
            cipherText2 = filtered.substring(cipherRand + saltRand, cipherRand + saltRand + cipherLen);
            saltCode2 = filtered.substring(cipherRand + saltRand + cipherLen);
        } else if (cipherRand < saltRand) {
            saltCode1 = filtered.substring(0, saltRand);
            cipherText2 = filtered.substring(saltRand, saltRand + cipherLen);
            saltCode2 = filtered.substring(saltRand + cipherLen, saltRand + cipherLen + saltLen);
            cipherText1 = filtered.substring(saltRand + cipherLen + saltLen);
        } else {
            cipherText2 = filtered.substring(0, cipherLen);
            saltCode1 = filtered.substring(cipherLen, cipherLen + saltRand);
            cipherText1 = filtered.substring(cipherLen + saltRand, cipherLen + saltRand + cipherRand);
            saltCode2 = filtered.substring(cipherLen + saltRand + cipherRand);
        }

        String[] elements = { saltCode1 + saltCode2, cipherText1 + cipherText2 };

        System.out.println("stageTwoEncrypted = " + stageTwoEncrypted);
        System.out.println("cipherLen = " + cipherLen);
        System.out.println("cipherRand = " + cipherRand);
        System.out.println("saltLen = " + saltLen);
        System.out.println("saltRand = " + saltRand);
        System.out.println("SALT = " + saltCode1 + saltCode2);
        System.out.println("ENCRYPTED PIN = " + cipherText1 + cipherText2);

        return elements;
    }

    public static String base7Decoder(List<Map<String, String>> mappings, String encodedString) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < encodedString.length(); i += 2) {
            String code = encodedString.substring(i, i + 2);
            String character = getCharacter(mappings, code);
            decoded.append(character);
        }
        return decoded.toString();
    }

    public static String getCharacter(List<Map<String, String>> mappings, String code) {
        for (Map<String, String> map : mappings) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals(code)) {
                    return entry.getKey();
                }
            }
        }
        return "";  // Code not found in the dictionary
    }

    public static String execute(String username, String password) throws Exception {
        String decodedBase7 = base7Decoder(mappedDictionary, password);
        String[] elements = decryptGateway(decodedBase7);

        String salt = elements[0];
        String dartEncryptedPIN = elements[1];
        System.out.println("DART ENCRYPTED PIN CODE ::: "+dartEncryptedPIN);
        String decryptedPass = decrypt(dartEncryptedPIN, username, salt);

        return decryptedPass;
    }


}
