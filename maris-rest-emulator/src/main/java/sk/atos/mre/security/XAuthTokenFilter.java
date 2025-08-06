package sk.atos.mre.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import sk.atos.mre.service.JwtUserDetailsServiceImpl;

public class XAuthTokenFilter implements Filter {
	
	private final static String TOKEN_HEADER = "Authorization";
	
	private final static String TOKEN_PREFIX = "Bearer ";
	
	private JwtTokenUtil jwtUtils = new JwtTokenUtil();
		
	private UserDetailsService userDetailsService = new JwtUserDetailsServiceImpl();
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
			    
		HttpServletRequest request = (HttpServletRequest) servletRequest;
	    HttpServletResponse response = (HttpServletResponse) servletResponse;
	    
	    String authToken = request.getHeader(TOKEN_HEADER);
	    String username = null; 
	    
	    if (authToken != null) {
	    	authToken = authToken.startsWith(TOKEN_PREFIX) ? authToken.substring(7) : authToken;
	    	username = this.jwtUtils.getUsernameFromToken(authToken);
	    }
	    	    
		if ( username != null ) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtUtils.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}			
		} else {
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() { }

	@Override
	public void init(FilterConfig arg0) throws ServletException { }
}