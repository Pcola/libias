package sk.atos.mre.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
	
	private static final long serialVersionUID = -4872416369128288120L;
			
	private static String secret = "asdasvlkmer5487et5rev";
	private static long expiration = 86400L;
		
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_AUDIENCE = "audience";
    private static final String AUDIENCE_UNKNOWN = "unknown";
	
	public String getUsernameFromToken(String token) {
    	String username = null;
    	try {
    		Claims claims = this.getClaimsFromToken(token);
    		username = (String) claims.get(CLAIM_KEY_USERNAME);
    	} catch (Exception e) {
    		username = null;
    	}
    	return username;
    }
	
	private Claims getClaimsFromToken(String token) {
        try {
        	return Jwts.parser()
        			.setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
			return null;
		}
    }
	
	public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }
	
	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + (expiration * 1000));
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_AUDIENCE, AUDIENCE_UNKNOWN);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

	String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
        		.setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
	
	public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }
	
	public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token));
    }
	
	private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
	
	public Boolean validateToken(String token, UserDetails userDetails) {
        SecurityUser user = (SecurityUser) userDetails;
        final String username = getUsernameFromToken(token);
                
        return (
        	username.equals(user.getUsername()) && !isTokenExpired(token)
        );
    }
	
}