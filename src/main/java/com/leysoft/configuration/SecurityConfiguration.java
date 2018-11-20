
package com.leysoft.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.leysoft.filter.JwtAuthenticationFilter;
import com.leysoft.filter.JwtAuthorizationFilter;
import com.leysoft.service.inter.JwtService;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value(
            value = "${security.matchers.login}")
    private String loginPath;

    @Value(
            value = "${security.matchers.anonymous}")
    private String[] anonymousMatchers;

    @Value(
            value = "${security.matchers.public}")
    private String[] publicMatchers;

    @Value(
            value = "${security.matchers.user}")
    private String[] userMatchers;

    @Value(
            value = "${security.matchers.admin}")
    private String[] adminMatchers;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(publicMatchers).permitAll()
                .antMatchers(anonymousMatchers).anonymous().antMatchers(userMatchers)
                .access("hasRole('ROLE_USER')").antMatchers(adminMatchers)
                .access("hasRole('ROLE_ADMIN')").anyRequest().authenticated().and()
                .addFilter(new JwtAuthenticationFilter(this.authenticationManager(), jwtService,
                        loginPath))
                .addFilter(new JwtAuthorizationFilter(this.authenticationManager(), jwtService))
                .csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    protected AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
