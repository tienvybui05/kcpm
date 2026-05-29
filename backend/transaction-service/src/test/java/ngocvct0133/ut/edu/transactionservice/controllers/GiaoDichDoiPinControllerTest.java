package ngocvct0133.ut.edu.transactionservice.controllers;

import ngocvct0133.ut.edu.transactionservice.services.IGiaoDichDoiPinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GiaoDichDoiPinController.class)
class GiaoDichDoiPinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IGiaoDichDoiPinService giaoDichDoiPinService;

    @Test
    void createTransactionWithEmptyBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/transaction-service/giaodichdoipin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}