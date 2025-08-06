package sk.atos.fri.ws.maris.service;

import java.util.Calendar;
import sk.atos.fri.ws.maris.model.Token;

public class TokenStore {

	private static Token token = null;
	private static Long SAFETY_PERIOD_MS = 60000l;
		
	public static void setToken(Token newToken) {
		token = newToken;
	}
	
	public static Token getToken() {
		if (token == null) {
			return null;
		}
		
		Calendar expiration = Calendar.getInstance();
		expiration.setTime(token.getExpiration());
		
		Calendar now = Calendar.getInstance();
		
		if (now.getTimeInMillis() > (expiration.getTimeInMillis() - SAFETY_PERIOD_MS)) {
			return null;
		}
		
		return token;
	}

}
