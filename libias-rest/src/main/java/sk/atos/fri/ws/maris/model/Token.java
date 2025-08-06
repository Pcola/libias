package sk.atos.fri.ws.maris.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
	
	public String accessToken;
	public String tokenType;
	public Long expiresIn;
	public String scope;
	public Long nbf;
	public String userName;
	public String iss;
	public String correlationId;
	public Date expiration;
	public String jti;
	
	public Token () {}
	
	@JsonProperty("access_token")
	public String getAccessToken() {
		return accessToken;
	}
	@JsonProperty("access_token")
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@JsonProperty("token_type")
	public String getTokenType() {
		return tokenType;
	}
	@JsonProperty("token_type")
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	@JsonProperty("expires_in")
	public Long getExpiresIn() {
		return expiresIn;
	}
	@JsonProperty("expires_in")
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	@JsonProperty("scope")
	public String getScope() {
		return scope;
	}
	@JsonProperty("scope")
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@JsonProperty("nbf")
	public Long getNbf() {
		return nbf;
	}
	@JsonProperty("nbf")
	public void setNbf(Long nbf) {
		this.nbf = nbf;
	}
	
	@JsonProperty("user_name")
	public String getUserName() {
		return userName;
	}
	@JsonProperty("user_name")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@JsonProperty("iss")
	public String getIss() {
		return iss;
	}
	@JsonProperty("iss")
	public void setIss(String iss) {
		this.iss = iss;
	}
	
	@JsonProperty("correlation_id")
	public String getCorrelationId() {
		return correlationId;
	}
	@JsonProperty("correlation_id")
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
	@JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
	@JsonProperty("expiration")
	public Date getExpiration() { 
		return expiration;
	}
	@JsonProperty("expiration")
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
	@JsonProperty("jti")
	public String getJti() {
		return jti;
	}
	@JsonProperty("jti")
	public void setJti(String jti) {
		this.jti = jti;
	}
}