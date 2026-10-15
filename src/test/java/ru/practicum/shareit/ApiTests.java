package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void userLifecycleAndDuplicateEmail() throws Exception {
        long first = createUser("first@example.org");
        long second = createUser("second@example.org");
        mvc.perform(patch("/users/{id}", second).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Changed\",\"email\":\"first@example.org\"}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/users/{id}", second)).andExpect(jsonPath("$.name").value("Owner"));
        mvc.perform(patch("/users/{id}", first).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Changed\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("first@example.org"));
        mvc.perform(delete("/users/{id}", first)).andExpect(status().isOk());
        mvc.perform(get("/users/{id}", first)).andExpect(status().isNotFound());
        createUser("first@example.org");
    }

    @Test
    void validationAndMissingResources() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Owner\",\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        long owner = createUser("validation@example.org");
        mvc.perform(post("/items").header("X-Sharer-User-Id", owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"Tool\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/items/999999")).andExpect(status().isNotFound());
        mvc.perform(patch("/users/{id}", owner).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ownershipPartialUpdateAndSearch() throws Exception {
        long owner = createUser("items@example.org");
        long other = createUser("other@example.org");
        String body = mvc.perform(post("/items").header("X-Sharer-User-Id", owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"UniqueToolToken\",\"available\":true}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long item = mapper.readTree(body).get("id").asLong();
        mvc.perform(patch("/items/{id}", item).header("X-Sharer-User-Id", other)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Stolen\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/items").header("X-Sharer-User-Id", other))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/items/search").param("text", "uniquetooltoken"))
                .andExpect(jsonPath("$[0].id").value(item));
        mvc.perform(patch("/items/{id}", item).header("X-Sharer-User-Id", owner)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"available\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.available").value(false));
        mvc.perform(get("/items/search").param("text", "UniqueToolToken"))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/items/search").param("text", "   "))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/items/{id}", item).header("X-Sharer-User-Id", other))
                .andExpect(status().isOk());
    }

    private long createUser(String email) throws Exception {
        String body = mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Owner\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }
}
