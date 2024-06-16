package arch.patterns.coursework.oauth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String getHome() {
        return "home";
    }


    @GetMapping("/welcome")
    public String getWelcome(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        if (oidcUser != null) {
            model.addAttribute("name", oidcUser.getGivenName());
            model.addAttribute("surname", oidcUser.getFamilyName());
        }
        return "welcomePage";
    }

}
