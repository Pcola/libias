package sk.atos.mre.controller;

import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.mre.model.Token;
import sk.atos.mre.security.JwtTokenUtil;
import com.itextpdf.xmp.impl.Base64;
import java.util.ArrayList;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RestController
public class AuthentificationController {

    @Autowired
    private AuthenticationManager authenticationManager;
	
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
 
    
	@RequestMapping(value="oauth/token",
                    method=RequestMethod.POST,  
                    consumes = {"application/x-www-form-urlencoded; charset=UTF-8"},
                    produces={"application/json"})
	public ResponseEntity<Token> createAuthentificationToken(@RequestHeader("authorization") final String authorization, @RequestParam("client_id") String username, @RequestParam("client_secret") String password) {			
		
	  try {
			 String[] authHeader = authorization.split("\\s+");
			 
			 if (authHeader.length != 2) {
				 throw new IllegalArgumentException("authorization header malformed");
			 }
			 
			 String decoded = Base64.decode(authHeader[1]);
			 String[] credentials = decoded.split(":");
			 
			 if (credentials.length != 2) {
				 throw new IllegalArgumentException("authorization header malformed");
			 }
			 
			 if(username == null) {
				 throw new IllegalArgumentException("request body malformed"); 
			 }
			 
			 if(password == null) {
				 throw new IllegalArgumentException("request body malformed"); 
			 }
		 
			 if(!username.equals(credentials[0]) || !password.equals(credentials[1])) {
				 throw new IllegalArgumentException("credentials provided in authorization header not equal to those provided in request body");	
			 }
			 
			 System.out.println(username + " " + password);
			 			 
			 final Authentication authentication = this.authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(
							username,
							password,
							new ArrayList<>()
						)
					);
			 
			 SecurityContextHolder.getContext().setAuthentication(authentication);
			 final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			 System.out.println(userDetails);
			
			 final Token token = new Token();
			 String t = jwtTokenUtil.generateToken(userDetails);
			 token.setAccessToken(t);
			 token.setScope("all");
			 token.setTokenType("bearer");
			 token.setUserName(userDetails.getUsername());
			 token.setExpiration(jwtTokenUtil.getExpirationDateFromToken(t));
			 token.setExpiresIn(jwtTokenUtil.getExpirationDateFromToken(t).getTime() - Calendar.getInstance().getTimeInMillis());
			 token.setCorrelationId("oaJb");
			 token.setIss("localhost");
			 token.setJti("d8d379f5-cd7e-42fe-9a16-b1863f3cb57e");
			 token.setNbf(new Long(1532421515));
			
			 return ResponseEntity.ok(token);
			 
			 
	  } catch (IllegalArgumentException e) {
		  return ResponseEntity.badRequest().body(null);
	  } catch (AuthenticationException e) {
		  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	  } catch (Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	  }
	}	
}
