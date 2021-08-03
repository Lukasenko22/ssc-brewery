package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void deleteBeerWithUrlParamCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .param("Api-Key","Lukas").param("Api-Secret","1234"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerWithUrlParamBadCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .param("Api-Key","Lukas").param("Api-Secret","xxx"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .header("Api-Key","Lukas")
                .header("Api-Secret","xxxxx"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .header("Api-Key","Lukas")
                .header("Api-Secret","1234"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeer_withNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listBeers_withoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerById_withoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/97df0c39-90c4-4ae8-b663-453e8e19c311"))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerByUpc_withoutLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/8631234200036"))
                .andExpect(status().isOk());
    }
}