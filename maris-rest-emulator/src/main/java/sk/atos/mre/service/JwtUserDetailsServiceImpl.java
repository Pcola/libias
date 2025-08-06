package sk.atos.mre.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sk.atos.mre.security.SecurityUser;

@Service
public class JwtUserDetailsServiceImpl  implements UserDetailsService {
	
	private static final String USERNAME = "maris";
	private static final String PASSWORD = "$2a$04$SvkanlYMlVmoeWOQ4dI8Vea7qSpl5Ofu0Awfg/.63uzmrJwcn.bA2"; // maris123
		
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		if (username != null && username.equals(USERNAME)) {
			return new SecurityUser(USERNAME, PASSWORD);
		}		
		throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
	}	
}
