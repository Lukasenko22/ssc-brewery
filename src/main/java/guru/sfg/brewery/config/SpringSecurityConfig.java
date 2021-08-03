package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlParamAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class).csrf().disable();
        http.addFilterBefore(restUrlParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/","/webjars/**","/login","/resources/**").permitAll()
                    .antMatchers("/beers/find","/beers*").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/v1/beer/**").permitAll()
                    .mvcMatchers(HttpMethod.GET,"/api/v1/beerUpc/{upc}").permitAll();
        }).authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();

        // h2 console config
        http.headers().frameOptions().sameOrigin();
    }

      // First approach option
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("Lukas").password("1234").roles("ADMIN").build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user").password("password").roles("USER").build();
//
//        return new InMemoryUserDetailsManager(admin,user);
//    }

    // Second approach option
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("Lukas")
                .password("{bcrypt}$2a$10$alb6bC6Q5fNmsS1L0lGud.wk/yBWj8Q71XzZGmU4k8IIi4HYEujla")
                .roles("ADMIN")
                    .and()
                .withUser("user")
                .password("{sha256}e1cbf71228cb5c70404590bf3a74b85410fe631d9512fe6a2b3d9dd3edc2660f16b8f2508fe78808")
                .roles("USER")
                    .and()
                .withUser("scott")
                .password("{bcrypt10}$2a$10$SB5DOCXYmeDwH6YFcYZWaeheu8r6.a7TQ7mLjT7g6Jn9wtgH8Bv8e")
                .roles("CUSTOMER");
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher(("/api/**")));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestUrlParamAuthFilter restUrlParamAuthFilter(AuthenticationManager authenticationManager){
        RestUrlParamAuthFilter filter = new RestUrlParamAuthFilter(new AntPathRequestMatcher(("/api/**")));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}
