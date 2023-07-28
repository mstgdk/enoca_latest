package com.enoca.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException,
            IOException {
        // requestin içindeki JwtToken i elde edeceğiz. Çünkü JWT token requeste  gömülü olark gelecek
        String jwtToken = parseJwt(request);//requestten JWT tokeni aldık
          /*
          Currently Login olan kullanıcı için 2 yol var. ya security contexden alacağız ya da JWT tokendan emailini alırız
           */
        try {
            if(jwtToken!=null && jwtUtils.validateJwtToken(jwtToken)) {//yukarıda aldığımız Token null değilse ve valide edilmişse
                String email = jwtUtils.getEmailFromToken(jwtToken);// içinden emaili al
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);//email vasıtasıyla user bilgilerine ulaş. security de oplduğumuz için user değiş UserDetails

                // !!! Valide edilen user bilgilerini SecurityContext e gönderiyoruz
                UsernamePasswordAuthenticationToken authenticationToken = new
                        UsernamePasswordAuthenticationToken(userDetails,//kullanıcı bilgileri
                        null,
                        userDetails.getAuthorities());//rolleri
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);//SecurityContextHolder'a gönderdik
            }
        } catch (Exception e) {
            logger.error("User not Found{} : ", e.getMessage());
        }
        filterChain.doFilter(request,response);//bu işlemleri bir filtre olarak eklemesini belrtiyorum


    }

    private String parseJwt(HttpServletRequest request){// token ı almak için request yeterli. JWT Token gelen requestin HEADER'ında, onunda
        //Authorization bölümünün içindedir
        String header = request.getHeader("Authorization");//requestin headeri getir. Onunda içinde authorization ı getir

        if(StringUtils.hasText(header) && header.startsWith("Bearer ")) {//gelen benim istediğim JWT token mi kontrol ediyorum.
            // gelen header in içinde string yapı var mı ve Bearear ile başlıyor mu
            return header.substring(7);
        }

        return null;

    }

    @Override//bu path leri security den muaf tut
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return antPathMatcher.match("/register", request.getServletPath()) ||
                antPathMatcher.match("/login", request.getServletPath());
    }
}