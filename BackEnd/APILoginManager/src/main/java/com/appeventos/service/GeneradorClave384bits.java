package com.appeventos.service;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

public class GeneradorClave384bits {

    public static void main(String[] args) {
        SecretKey key = Jwts.SIG.HS384.key().build();
        System.out.println(Encoders.BASE64.encode(key.getEncoded()));
    }
}
