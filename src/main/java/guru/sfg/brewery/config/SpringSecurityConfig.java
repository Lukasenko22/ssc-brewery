package guru.sfg.brewery.config;

import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/h2-console/**")
                        .permitAll()
                    .antMatchers("/","/webjars/**","/login","/resources/**")
                        .permitAll()
                    .antMatchers("/beers/find","/beers*")
                        .hasAnyRole("ADMIN","CUSTOMER","USER")
                    .antMatchers("/brewery/breweries/**")
                        .hasAnyRole("ADMIN","CUSTOMER")
                    .antMatchers(HttpMethod.GET,"/api/v1/beer/**")
                        .hasAnyRole("ADMIN","CUSTOMER","USER")
                    .antMatchers(HttpMethod.DELETE,"/api/v1/beer/**")
                        .hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET,"/brewery/api/v1/breweries")
                        .hasAnyRole("ADMIN","CUSTOMER")
                    .mvcMatchers(HttpMethod.GET,"/api/v1/beerUpc/{upc}")
                        .hasAnyRole("ADMIN","CUSTOMER","USER");
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

    @Bean
    PasswordEncoder passwordEncoder(){
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
