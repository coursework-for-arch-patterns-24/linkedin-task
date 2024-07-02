package edu.agh.is.consolidate.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomePageController
{
    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OidcUser principal, Model model)
    {
        if(principal != null)
        {
            Map<String, Object> attributes = principal.getAttributes();

            String name = attributes.getOrDefault("given_name", "Unknown").toString();
            String surname = attributes.getOrDefault("family_name", "Unknown").toString();

            model.addAttribute("name", name);
            model.addAttribute("surname", surname);
        }

        return "index";
    }
}
