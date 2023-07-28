package com.enoca.security;

import com.enoca.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//method base çalıştığımız anlamına gelir // HASROLE yazabilmek için true ya setledik

public class SecurityConfig {
    // !!! AMACIM: Encoder,Provider ,AuthTokenFilter gibi yapıları oluşturmak

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean // security den gelen filtrelere yenisini ekliyooruz
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().//update post gibi metotları çalıştırabilmek için
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).// server-client iki tarafta birbiri hakkında bilgi tutmayacak ve birbirini tanımaz.
                and().// Alt satır-- CORS işlemlerinde delete gibi işlemkerde meydana gelen sorunu ortadan kaldırmakm için eklendi
                authorizeRequests().antMatchers(HttpMethod.OPTIONS,"/**").permitAll().and().
                authorizeRequests().
                antMatchers("/login", // bunları security katmanından muaf tut
                        "/register",
                        "/",
                        "/files/download/**",
                        "/files/display/**",
                        "/car/visitors/**",
                        "/contactmessage/visitors",
                        "/actuator/info","/actuator/health").permitAll().
                anyRequest().authenticated();

        // !!! AuthTokenFilter yazdıktan sonra addFilter yazılacak
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class); // bu filltrenin önüne benim oluşturduğum filtreyi ekle
        return http.build();
    }
//*******************SWAGGER***********************

    private static final String [] AUTH_WHITE_LIST= {
            "/v3/api-docs/**", // swagger
            "swagger-ui.html", //swagger
            "/swagger-ui/**", // swagger
            "/",
            "index.html",
            "/images/**",
            "/css/**",
            "/js/**"
            //bU ENDPOİNTLERİ KULLANABİLMEK için AŞAĞIDAKİ metodu yazdık ve security den muaf tuttu
    };

    // yukardaki static listeyi de giriş izni veriyoruz, boiler plate
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        WebSecurityCustomizer customizer=new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers(AUTH_WHITE_LIST);
            }
        };
        return customizer;
    }

    //**************************************************************************
    // !!! Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // !!! Provider
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    // !!! AuthenticationManager
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).
                authenticationProvider(authProvider()).
                build();
    }

    // !!! AutTokenFilter ( JWT token üreten ve valide eden class )
    //Security default olarak 15 adet filtre uygular. Bu metot sayesinde 16. filtreti de custom olarak ben oluşturuyorum
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(); //metot return etti
    }

}
