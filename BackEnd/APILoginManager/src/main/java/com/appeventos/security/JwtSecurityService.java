package com.appeventos.security;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtSecurityService {

    @Value("${jwt.secret}")//secret esta en el application.properties, con @Value la inyecto
    private String secret;
    
    @Value("${jwt.expiration}")//expiration esta en el applicationLogin.properties
    private Long expirationMs; //en Milisegundos a sumar desde la fecha/hora actual
    
    //1. CREAR LA CLAVE SECRETA, coge el secret y lo decodifica
    private SecretKey signingKey() {
    	
    	//El secret en BASE64, lo decofica y lo prepara para HMAC SHA256 o superior
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    //2. GENERAR EL TOKEN
    public String generateToken (String username, Collection<? extends GrantedAuthority> auths) {
    	Map <String, Object> claims = Map.of(
    			"roles", auths.stream()
    			.map(GrantedAuthority::getAuthority)
    			.toList()
    			);
   	
    	return Jwts.builder()
    			.claims(claims)//le meto los claims, lleva los roles. 
    			.subject(username)
    			.issuedAt(new Date(System.currentTimeMillis())) 
    			.expiration(new Date(System.currentTimeMillis()+expirationMs)) //expritationMs en el applicationLogin.propertie
    			.signWith(signingKey(), Jwts.SIG.HS384) //tercera parte token, secret decodificado (mas encriptacion en SHA384)
    			.compact();
    }
/*
 * Partes del Jwts (JSON Web Token) 
 * 		HEADER: algoritmo encriptacion (HS256) y el tipo token (Jwts)
 * 		DECODED PLAYLOAD: claims (username, sub o userIde, rol o admin true | false, fecha expiracon
 * 		SECRET: clave secreta lo mas grande posible, al menos de 256bits. Deriva secret del EduardoLazaroPeonActividad2ApplicationLogin.propertie
 * 
 *  Detalles en: https://www.jwt.io/
 */

    //-------Estos metodos se usan en el JwtAuthenticationFilter------//
    //3. VERIFICACION DEL TOKEN
  	public boolean isTokenValid(String token) {
      	try {
      		Jwts.parser()
      		.verifyWith(signingKey())
      		.build()
      		.parseSignedClaims(token);
      		
      		return true;
      	}catch (io.jsonwebtoken.security.SignatureException e) {
  			System.out.println("Token JWT expirado");
  			return false;
  			
  		}catch (Exception e) {
  			System.out.println("Token JWT invalido");
  			return false;
  		}
      }
    
  	//4. DEL TOKEN EXTRAE EL USUARIO
    public String extractUsername(String token) {
    	return Jwts.parser()
    			.verifyWith(signingKey()) //.setSigningKey -> deprecado en la version 0.12.5 del dependency jsonwebtoken en el pom.xml
    			.build()
    			.parseSignedClaims(token) //.parseClaimsJws -> deprecado en la version 0.12.5 
    			.getPayload() // .getBody -> deprecado
    			.getSubject(); //devuelve username (email)
    }
}
