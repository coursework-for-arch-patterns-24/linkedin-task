package pl.wojdylak.linkedin_task.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "login/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                )
                .oauth2Login(oauthLoginConfig -> oauthLoginConfig
                        .loginPage("/login")
                        .authorizationEndpoint((authorizationEndpointConfig ->
                                authorizationEndpointConfig.authorizationRequestResolver(
                                        requestResolver(this.clientRegistrationRepository)
                                ))
                        )
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig
                                .accessTokenResponseClient(linkedinTokenResponseClient())
                        )
                );

        return httpSecurity.build();
    }

    private static DefaultAuthorizationCodeTokenResponseClient linkedinTokenResponseClient() {

        var defaultMapConverter = new DefaultMapOAuth2AccessTokenResponseConverter();
        var httpConverter = getResponseHttpMessageConverter(defaultMapConverter);

        var restOperations = new RestTemplate(List.of(new FormHttpMessageConverter(), httpConverter));
        restOperations.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        var client = new DefaultAuthorizationCodeTokenResponseClient();
        client.setRestOperations(restOperations);
        return client;
    }

    private static OAuth2AccessTokenResponseHttpMessageConverter getResponseHttpMessageConverter(DefaultMapOAuth2AccessTokenResponseConverter defaultMapConverter) {

        Converter<Map<String, Object>, OAuth2AccessTokenResponse> linkedinMapConverter = tokenResponse -> {
            var withTokenType = new HashMap<>(tokenResponse);
            withTokenType.put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2AccessToken.TokenType.BEARER.getValue());
            return defaultMapConverter.convert(withTokenType);
        };

        var httpConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        httpConverter.setAccessTokenResponseConverter(linkedinMapConverter);
        return httpConverter;
    }

    private static DefaultOAuth2AuthorizationRequestResolver requestResolver(ClientRegistrationRepository clientRegistrationRepository) {

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