package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Test
    void listBreweriesWithAuthenticationWithCustomerAndAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk())
                .andExpect(view().name("breweries/index"))
                .andExpect(model().attributeExists("breweries"));
    }

    @Test
    void listBreweriesWithAuthenticationWithCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isOk())
                .andExpect(view().name("breweries/index"))
                .andExpect(model().attributeExists("breweries"));
    }

    @Test
    void listBreweriesWithAuthenticationWithoutCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void initCreationFormWithSpring() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/new")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/new")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void initCreationFormWithScott() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/new")
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findBeers_Lukas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers_User() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
                .with(httpBasic("user","password")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers_Scott() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers_withHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers_unauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers/find"))
                .andExpect(status().isUnauthorized());
    }
}
