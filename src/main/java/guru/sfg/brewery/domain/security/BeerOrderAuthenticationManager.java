package guru.sfg.brewery.domain.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BeerOrderAuthenticationManager {

    public boolean customerIdMatches(Authentication auth, UUID customerId){
        User authenticatedUser = (User) auth.getPrincipal();
        log.debug("Auth User Customer Id: "+ authenticatedUser.getCustomer().getId() + " - Customer Id: "+customerId);
        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
}
