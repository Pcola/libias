package sk.atos.fri.security.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sk.atos.fri.security.hmac.HmacRequester;
import sk.atos.fri.security.hmac.HmacSecurityConfigurer;
import sk.atos.fri.security.service.AuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("libias")
  private DataSource dataSource;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private HmacRequester hmacRequester;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/actuator/**").anonymous()
        .antMatchers("/actuator").anonymous()
        .antMatchers("/dataImport/**").anonymous()
        .antMatchers("/login").anonymous()
        .antMatchers("/**").authenticated()
        .and()
        .csrf()
        .disable()
        .headers()
        .frameOptions().disable()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .logout().disable()
        .apply(authTokenConfigurer())
        .and()
        .apply(hmacSecurityConfigurer())
        .and()
        .formLogin().disable();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .jdbcAuthentication().dataSource(dataSource)
        .passwordEncoder(passwordEncoder())
        .usersByUsernameQuery("select username, password, active from BAM_USER where username=?")
        .authoritiesByUsernameQuery("SELECT u.username, r.role from BAM_USER u join USER_ROLE2BAM_USER ru on ru.USER_ID=u.USER_ID join USER_ROLE r on ru.ROLE_ID=r.ROLE_ID where u.username=?");
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  private HmacSecurityConfigurer hmacSecurityConfigurer() {
    return new HmacSecurityConfigurer(hmacRequester);
  }

  private XAuthTokenConfigurer authTokenConfigurer() {
    return new XAuthTokenConfigurer(authenticationService);
  }

}
