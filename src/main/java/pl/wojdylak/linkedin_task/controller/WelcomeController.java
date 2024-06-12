package pl.wojdylak.linkedin_task.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String getHomePage(@AuthenticationPrincipal OidcUser principal,
                          Model model) {

        if (principal != null) {
            Map<String, Object> attributes = principal.getAttributes();

            String name = attributes.getOrDefault("localizedFirstName", attributes.getOrDefault("given_name", "Unknown")).toString();
            String surname = attributes.getOrDefault("localizedLastName", attributes.getOrDefault("family_name", "Unknown")).toString();

            model.addAttribute("name", name);
            model.addAttribute("surname", surname);
        }

        return "index";
    }


    @GetMapping("/login")
    public String getLoginPage() {

        return "login";
    }
}