package com.wooeen.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Decodificador {

    public static String hash256(String decodificada){
        if(decodificada == null)
            return null;

        byte[] hash = gerarHash(decodificada);

        return stringHexa(hash);
    }

    public static String md5(String value) {
        MessageDigest m;
        try {
            m=MessageDigest.getInstance("MD5");
            m.update(value.getBytes(),0,value.length());
            return new BigInteger(1,m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) s.append('0');
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    private static byte[] gerarHash(String frase) {
        return gerarHash(frase, "SHA-256");
    }

    private static byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(frase.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
