package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {

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