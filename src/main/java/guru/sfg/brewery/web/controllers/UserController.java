package guru.sfg.brewery.web.controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    public static final String REGISTER_2FA_VIEW = "user/register2fa";
    public static final String VERIFY_2FA_VIEW = "user/verify2fa";

    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model){
        User user = getUser();

        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("LM",user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: "+url);

        model.addAttribute("googleurl",url);
        return REGISTER_2FA_VIEW;
    }

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode){
        User user = getUser();

        log.debug("Entered Code is: "+verifyCode);
        if (googleAuthenticator.authorizeUser(user.getUsername(),verifyCode)){
            User savedUser = userRepository.findById(user.getId()).orElseThrow();

            savedUser.setUseGoogle2f(true);
            userRepository.save(savedUser);

            return "redirect:/";
        }
        return "redirect:/user/register2fa";
    }

    @GetMapping("/verify2fa")
    public String verify2fa(){
        return VERIFY_2FA_VIEW;
    }

    @PostMapping("/verify2fa")
    public String verifyPostOf2fa(@RequestParam Integer verifyCode){
        User user = getUser();

        if (googleAuthenticator.authorizeUser(user.getUsername(),verifyCode)) {
            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2faRequired(false);
            return "redirect:/";
        }

        return "redirect:/"+VERIFY_2FA_VIEW;
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
