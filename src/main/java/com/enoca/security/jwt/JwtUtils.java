package com.enoca.security.jwt;

import com.enoca.exception.message.ErrorMessage;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component//Bu classdan ilerde obje isteyeceğim
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    /* NEDEN LOGLADIK: önemli işlemleri loglamalıyız. JWT hayati bir işlemdir ve güvenlikle ilgilidir.
     */


    @Value("${enoca.app.jwtSecret}") // değeri git buradan al.
    private String jwtSecret ;// bu değişkeni app.yml dosyasından alıyoruz
    @Value("${enoca.app.jwtExpirationMs}")
    private Long jwtExpirationMs ;

    // !!! generate JWT token
    public String generateJwtToken(UserDetails userDetails){//kullanıcıdan bilgi almamız için parametre olarak userDtails yazdık
        return Jwts.builder().
                setSubject(userDetails.getUsername()).//emaili alıyoruz. diğer classda emaili username setlemiştik
                        setIssuedAt(new Date()).//oluşturulma zamanı
                        setExpiration(new Date(new Date().getTime() + jwtExpirationMs)).//
                        signWith(SignatureAlgorithm.HS512, jwtSecret).//şifreleme yöntemi ve secretKey i de kullanarak
                        compact();
    }
    /*
    uniq değer email olduğu için burada email kullandık.
     */



    // !!! JWT token içinden email bilgisine ulaşacağım method
    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).
                parseClaimsJws(token).
                getBody().
                getSubject();//Yukarıda setSubject dediğimiz yerdeki email bilgisi gelir
    }//JWTToken üzerinden Currently Login olan kullanıcılara ulaşabilirirm. Ayrıca Security Contex e atacağım, oradan da ulaşabiliririm



    // !!! JWT validate
    public boolean validateJwtToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);//token ın 1 ve 2.kısmının secretKey ile valide edilp 3. kısım ile karşılaştırıldığı yer
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            logger.error(String.format(
                    ErrorMessage.JWTTOKEN_ERROR_MESSAGE, e.getMessage()));
        }
        return false;
    }

}
