package arch.patterns.coursework.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 ->
                        oauth2
                            .authorizationEndpoint(authorization -> authorization
                                    .authorizationRequestResolver(
                                            requestResolver(this.clientRegistrationRepository)
                                    )
                            )
                            .defaultSuccessUrl("/welcome", true)

                );


        return http.build();
    }

    private static OAuth2AuthorizationRequestResolver requestResolver
            (ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver requestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization");
        requestResolver.setAuthorizationRequestCustomizer(config ->
                config.attributes(stringObjectMap -> stringObjectMap.remove(OidcParameterNames.NONCE))
                        .parameters(params -> params.remove(OidcParameterNames.NONCE))
        );
        return requestResolver;
    }
}
