package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ClientTests {
    @Test
    void preservesServerErrorStatusAndJson() {
        var customizer = new MockServerRestTemplateCustomizer();
        var client = new UserClient("http://server:9090", new RestTemplateBuilder(customizer));
        customizer.getServer().expect(requestTo("http://server:9090/users/42"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"Duplicate email\"}"));
        var response = client.update(42, new UserDto());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat((byte[]) response.getBody()).asString().contains("Duplicate email");
        customizer.getServer().verify();
    }

    @Test
    void forwardsIdentityAndEscapesSearchQuery() {
        var customizer = new MockServerRestTemplateCustomizer();
        var client = new ItemClient("http://server:9090", new RestTemplateBuilder(customizer));
        customizer.getServer().expect(requestTo("http://server:9090/items/search?text=drill%20%26%20saw"))
                .andExpect(method(HttpMethod.GET)).andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        assertThat(client.search(7, "drill & saw").getStatusCode()).isEqualTo(HttpStatus.OK);
        customizer.getServer().verify();
    }
}
