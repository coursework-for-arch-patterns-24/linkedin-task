package edu.agh.is.consolidate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests( auth -> auth
                    .requestMatchers("/", "/index.html").permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2Login(oauthLoginConfig -> oauthLoginConfig
                .authorizationEndpoint((authorizationEndpointConfig ->
                        authorizationEndpointConfig.authorizationRequestResolver(
                                requestResolver(this.clientRegistrationRepository)
                        ))
                ).failureHandler(new SimpleUrlAuthenticationFailureHandler("/")));

        return http.build();
    }

    private static DefaultOAuth2AuthorizationRequestResolver requestResolver
            (ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver requestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization");
        requestResolver.setAuthorizationRequestCustomizer(c ->
                c.attributes(stringObjectMap -> stringObjectMap.remove(OidcParameterNames.NONCE))
                        .parameters(params -> params.remove(OidcParameterNames.NONCE))
        );

        return requestResolver;
    }

}
