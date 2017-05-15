package api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String ADMIN = "ADMIN";
	private static final String USER = "USER";

	@Autowired
	DataSource dataSource;

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery(
				"select username,password, enabled from users where username=?")
		.authoritiesByUsernameQuery(
				"select username, role from user_roles where username=?");
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests()
		.antMatchers("/me", "/bower_components/**").permitAll()
		.antMatchers("/private/*").hasRole(USER)
		.antMatchers("/api/**").hasRole(USER)
		.antMatchers("/admin/*").hasRole(ADMIN)
//		.anyRequest().authenticated()
		.and()
		.formLogin().loginPage("/#!/login")
//		.defaultSuccessUrl("/", false)
		.and()
//		.exceptionHandling()
//		.accessDeniedPage("/error")
//		.and()
		.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

	}

}
