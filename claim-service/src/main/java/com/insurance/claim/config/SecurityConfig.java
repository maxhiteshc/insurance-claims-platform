package com.insurance.claim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  return http.csrf(csrf -> csrf.disable())
   .authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health","/actuator/info").permitAll().anyRequest().authenticated())
   .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(realmRoleConverter())))
   .build();
 }
 @Bean Converter<Jwt, ? extends AbstractAuthenticationToken> realmRoleConverter() {
  return jwt -> new JwtAuthenticationToken(jwt, authorities(jwt), jwt.getClaimAsString("preferred_username"));
 }
 private Collection<GrantedAuthority> authorities(Jwt jwt) {
  Collection<GrantedAuthority> result = new ArrayList<>();
  Map<String,Object> realmAccess = jwt.getClaimAsMap("realm_access");
  if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles)
   roles.forEach(role -> result.add(new SimpleGrantedAuthority("ROLE_" + role)));
  return result;
 }
}
