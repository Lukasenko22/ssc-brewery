package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void listBreweriesWithCustomerAndAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk());
    }

    @Test
    void listBreweriesWithCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void listBreweriesWithoutCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesWithoutAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerHttpBasicUserRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/"+beerToDelete().getId())
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/"+beerToDelete().getId())
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isForbidden());
    }

    public Beer beerToDelete(){
        Random rand = new Random();
        return beerRepository.saveAndFlush(Beer.builder()
                .beerName("Delete Me Beer")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(String.valueOf(rand.nextInt(99999999)))
                .build());
    }

    @Test
    @Disabled
    void deleteBeerWithUrlParamCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .param("Api-Key","Lukas").param("Api-Secret","1234"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    void deleteBeerWithUrlParamBadCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .param("Api-Key","Lukas").param("Api-Secret","xxx"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .header("Api-Key","Lukas")
                .header("Api-Secret","xxxxx"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled
    void deleteBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .header("Api-Key","Lukas")
                .header("Api-Secret","1234"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeer_withNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/"+beerToDelete().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBeerById_withLogin_Lukas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/"+beerToDelete().getId())
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerById_withLogin_User() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/"+beerToDelete().getId())
                .with(httpBasic("user","password")))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerById_withLogin_Scott() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/"+beerToDelete().getId())
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void listBeers_withLogin_Scott() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer")
                .with(httpBasic("scott","tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerById_withoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/"+beerToDelete().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBeerByUpc_withoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/"+beerToDelete().getUpc()))
                .andExpect(status().isUnauthorized());
    }
}