package com.optculture.app.config;

import com.clickhouse.client.internal.grpc.netty.shaded.io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		prePostEnabled = true,
		securedEnabled = true,
		jsr250Enabled = true)
public class SecurityConfiguration {

    private static final String[] PUBLIC = new String[] {
			"/test/**",
			"/api/ereceipt/**",
			"/api/login/**",
			"/actuator",
			"/actuator/**",
			"/api/segment/**",
			"/api/auto-recommendations/**",
			"/api/bee/**",
			"/api/campaigns/**",
			"/api/custom-rows/**",
			"/api/service/**"
		};

	@Bean(name = "authenticationManagerBean")
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@Autowired
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(authorize -> authorize
			.requestMatchers(PUBLIC).permitAll()
			// .requestMatchers(HttpMethod.DELETE).hasAuthority("ADMIN") 	// If UserDetails.getAuthorities return [ADMIN, ...]
			//.requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")		// If UserDetails.getAuthorities return [ROLE_ADMIN, ...] 
			// .requestMatchers("/hello").hasAuthority("OC Admin") //TODO
			.anyRequest().authenticated()
		).sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authenticationProvider(authenticationProvider())
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
		.build();
	}


    private AuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}
