package guru.sfg.brewery.domain.security.perms.order;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.read') OR " +
        "hasAuthority('customer.order.read') AND " +
        "@beerOrderAuthenticationManager.customerIdMatches(authentication,#customerId)")
public @interface BeerReadOrderPermission {
}