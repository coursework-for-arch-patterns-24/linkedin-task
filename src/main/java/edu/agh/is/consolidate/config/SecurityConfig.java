package edu.agh.is.consolidate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    protected SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception
    {
        // clang-format off
        http.authorizeHttpRequests(request_config -> request_config
                .requestMatchers("/", "images/**")
                    .permitAll()
                .anyRequest()
                    .authenticated())
            .oauth2Login(oauth2login_config -> oauth2login_config
                .loginPage("/")
                    .authorizationEndpoint(authorization_config -> authorization_config
                        .authorizationRequestResolver(
                            requestResolver(this.clientRegistrationRepository))));
        return http.build();
        // clang-format on
    }

    private static DefaultOAuth2AuthorizationRequestResolver requestResolver(
        ClientRegistrationRepository clientRegistrationRepository)
    {
        DefaultOAuth2AuthorizationRequestResolver requestResolver =
            new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                "/oauth2/authorization");

        requestResolver.setAuthorizationRequestCustomizer(authorization_config
            -> authorization_config
                   .attributes(
                       attributes_consumer -> attributes_consumer.remove(OidcParameterNames.NONCE))
                   .parameters(parameters_consumer
                       -> parameters_consumer.remove(OidcParameterNames.NONCE)));

        return requestResolver;
    }
}
