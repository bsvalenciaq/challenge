package co.com.challenge.healthcontroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HealthController healthController; // Mockito crea la instancia e inyecta mocks si los hubiera

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
    }

    @Test
    void serviceOk_returns200AndExpectedBody() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("Services Online"));
    }
}