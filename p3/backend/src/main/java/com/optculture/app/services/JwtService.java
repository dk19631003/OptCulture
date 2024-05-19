package com.optculture.app.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.optculture.shared.entities.org.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	@Value("${jwt.customer.token.expiration}")
	private Long customerTokenExpiration;
	
	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String encryptMap(Map<String, String> map) {

		JwtBuilder builder = Jwts.builder();

		for (var item : map.entrySet()) {
			builder.claim(item.getKey(), item.getValue());
		}

		return builder.setExpiration(new Date(System.currentTimeMillis() + customerTokenExpiration))
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512).compact();
	}

	public String generateToken(User user, List<String> authorities) {
		
		return Jwts.builder()
			.setSubject(user.getUserName())
			.claim("userId", user.getUserId())
			.claim("authority", authorities)
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(getKey(), SignatureAlgorithm.HS512)
			.compact();
	}
	
	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
	}

}
