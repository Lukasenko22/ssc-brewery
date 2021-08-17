package guru.sfg.brewery.domain.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event){
        log.debug("User failed to log in");
        if (event.getException() != null){
            log.debug("Login failed: "+event.getException().getMessage());
        }

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();
            if (token.getPrincipal() instanceof String){
                String username = (String) token.getPrincipal();
                loginFailureBuilder.username(username);
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()){
                    User user = userOpt.get();
                    log.debug("Existing user tried to log in: "+user.getUsername());
                    loginFailureBuilder.user(user);
                } else {
                    log.debug("Unknown user tried to log in: "+username);
                }
            }

            if (token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginFailureBuilder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: "+details.getRemoteAddress());
            }
            LoginFailure loginFailureSaved = loginFailureRepository.save(loginFailureBuilder.build());
            log.debug("LoginFailure saved: "+loginFailureSaved.getId());

            if (loginFailureSaved.getUser() != null){
                lockUserAccount(loginFailureSaved.getUser());
            }
        }
    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failureList = loginFailureRepository
                .findAllByUserAndCreatedDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if (failureList.size() > 3){
            log.debug("Locking user account...");
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }
}
