package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerIT extends  BaseIT {

    @Test
    void processCreationForm_adminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers/new").with(csrf())
                .param("customerName","Foo Customer")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    void processCreationForm_notAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers/new")
                .param("customerName","Foo Customer2")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());

    }

    @Test
    void processCreationForm_notAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/customers/new").with(csrf())
                .param("customerName","Foo Customer3"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void listCustomers_forbiddenForOtherThanCustomer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listCustomers_CustomerRoleAUTH() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .with(httpBasic("Lukas","1234")))
                .andExpect(status().isOk());
    }

    @Test
    void listCustomers_notLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andExpect(status().isUnauthorized());
    }
}
