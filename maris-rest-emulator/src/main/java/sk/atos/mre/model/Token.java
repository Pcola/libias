package sk.atos.mre.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
	
	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("token_type")
	public String tokenType;
	@JsonProperty("expires_in")
	public Long expiresIn;
	public String scope;
	public Long nbf;
	@JsonProperty("user_name")
	public String userName;
	public String iss;
	@JsonProperty("correlation_id")
	public String correlationId;
	public Date expiration;
	public String jti;
	
	public Token() {}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	public Long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public Long getNbf() {
		return nbf;
	}
	public void setNbf(Long nbf) {
		this.nbf = nbf;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getIss() {
		return iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	
	public String getCorrelationId() {
		return correlationId;
	}
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
	@JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
	public Date getExpiration() {
		return expiration;
	}
	
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	

}
