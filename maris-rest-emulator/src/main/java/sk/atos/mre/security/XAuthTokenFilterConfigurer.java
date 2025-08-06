package sk.atos.mre.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class XAuthTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	
	@Override
	public void configure(HttpSecurity builder) throws Exception {
		XAuthTokenFilter xAuthTokenFilter = new XAuthTokenFilter();
	    builder.addFilterBefore(xAuthTokenFilter, BasicAuthenticationFilter.class);
	}
}
