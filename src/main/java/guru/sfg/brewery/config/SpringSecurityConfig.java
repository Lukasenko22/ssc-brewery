package guru.sfg.brewery.config;

import guru.sfg.brewery.security.google.Google2faFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Google2faFilter google2faFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(google2faFilter, SessionManagementFilter.class);

        http.csrf().ignoringAntMatchers("/h2-console/**","/api/**");
        http.cors()
                .and()
                .authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/h2-console/**")
                        .permitAll()
                    .antMatchers("/","/webjars/**","/login","/resources/**")
                        .permitAll();
        }).authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(loginConfigurer -> {
                    loginConfigurer.loginProcessingUrl("/login")
                            .loginPage("/")
                            .permitAll()
                            .successForwardUrl("/")
                            .defaultSuccessUrl("/")
                            .failureUrl("/?error");
                }).logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
                            .logoutSuccessUrl("/?logout")
                            .permitAll();
                })
                .httpBasic()
                .and()
                .rememberMe()
                .tokenRepository(persistentTokenRepository)
                .userDetailsService(userDetailsService);
//                .rememberMe()
//                .key("lukas-remember-key")
//                .userDetailsService(userDetailsService);

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
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());
//
//        auth.inMemoryAuthentication()
//                .withUser("Lukas")
//                .password("{bcrypt}$2a$10$alb6bC6Q5fNmsS1L0lGud.wk/yBWj8Q71XzZGmU4k8IIi4HYEujla")
//                .roles("ADMIN")
//                    .and()
//                .withUser("user")
//                .password("{sha256}e1cbf71228cb5c70404590bf3a74b85410fe631d9512fe6a2b3d9dd3edc2660f16b8f2508fe78808")
//                .roles("USER")
//                    .and()
//                .withUser("scott")
//                .password("{bcrypt10}$2a$10$SB5DOCXYmeDwH6YFcYZWaeheu8r6.a7TQ7mLjT7g6Jn9wtgH8Bv8e")
//                .roles("CUSTOMER");
//    }

}
