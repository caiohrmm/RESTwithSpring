package br.com.caiohenrique.security.jwt;

import br.com.caiohenrique.data.valueobjects.v1.security.TokenVO;
import br.com.caiohenrique.exceptions.InvalidJWTAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {


    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long tokenExpirationTimeInMs = 3600000; //1h


    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct // Permite eu executar uma acao apos a execucao do spring
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenVO createAccessToken (String username, List<String> roles) {
        Date now = new Date(); // Momento atual
        Date validity = new Date(now.getTime() + tokenExpirationTimeInMs); // Momento daqui 1h
        var accessToken = getAccessToken(username, roles, now, validity);
        var refreshToken = getRefreshToken(username, roles, now, validity);
        return new TokenVO(username, true, now, validity, accessToken, refreshToken);

    }

    // Após expirar o token convencional, esse método gera outro access token, sem precisar reenviar user e password
    public TokenVO refreshToken (String refreshToken) {
        // Extrai o Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJz e fica somente o token
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());

        // Preciso decodificar o meu token para extrair seu subject e suas roles.
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);

        // Extraindo o username
        String username = decodedJWT.getSubject();

        // Extraindo as roles para lista de strings.
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        return createAccessToken(username, roles);

    }

    private String getRefreshToken(String username, List<String> roles, Date now, Date validity) {
        Date validityRefreshToken = new Date(now.getTime() + (tokenExpirationTimeInMs * 3));
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validityRefreshToken)
                .withSubject(username)
                .sign(algorithm)
                .strip();
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        // retorna a url do servidor
        String issueUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(username)
                .withIssuer(issueUrl)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities()
        );
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm algorithm1 = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm1).build();

        // Decodificando o token
        return jwtVerifier.verify(token);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public Boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            return !decodedJWT.getExpiresAt().before(new Date());
        }catch (Exception e) {
            throw new InvalidJWTAuthenticationException("Expired or invalid JWT token!");
        }
    }



}
