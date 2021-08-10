package de.hhu.covidtracer.security.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static de.hhu.covidtracer.security.UserRole.*;
import static de.hhu.covidtracer.security.UserRole.ADMIN;

@Profile({"prod", "dev", "postgres"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();
    }


    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password123"))
                .roles(ADMIN.name())
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles(USER.name())
                .build();

        return new InMemoryUserDetailsManager(
                admin,
                user
        );
    }
}
