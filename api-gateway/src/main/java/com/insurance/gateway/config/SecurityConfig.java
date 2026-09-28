package com.insurance.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(e->e.pathMatchers("/actuator/health","/actuator/info").permitAll().anyExchange().authenticated()).oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(realmRoleConverter()))).build();}
 @Bean Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> realmRoleConverter(){return jwt->Mono.just(new JwtAuthenticationToken(jwt,authorities(jwt),jwt.getClaimAsString("preferred_username")));}
 private Collection<GrantedAuthority> authorities(Jwt jwt){Collection<GrantedAuthority> r=new ArrayList<>();Map<String,Object> a=jwt.getClaimAsMap("realm_access");if(a!=null&&a.get("roles") instanceof Collection<?> roles)roles.forEach(x->r.add(new SimpleGrantedAuthority("ROLE_"+x)));return r;}
}
