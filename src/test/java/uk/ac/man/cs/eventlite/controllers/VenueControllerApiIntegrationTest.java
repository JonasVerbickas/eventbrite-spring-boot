package uk.ac.man.cs.eventlite.controllers;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.EventLite;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenueControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;
	
	private String base_url;
	
    private String login_url;
	
	private HttpEntity<String> httpEntity;
	
	@Autowired
	private TestRestTemplate template;

	private WebTestClient client;
	
	private final TestRestTemplate bad_example = new TestRestTemplate("Bad", "Person");

	@BeforeEach
	public void setup() {
		HttpHeaders head = new HttpHeaders();
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api").build();
		
		head.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		
		this.base_url = "http://localhost:" + port + "/venues";
		
		this.login_url = "http://localhost:" + port + "/sign-in";
		
		httpEntity = new HttpEntity<String>(head);
	}

	@Test
	public void testGetAllVenues() {
		client.get().uri("/venues").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$._links.self.href");
	}

	@Test
	public void getVenueNotFound() {
		client.get().uri("/venues/1011").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.error")
				.value(containsString("venue 1011")).jsonPath("$.id").isEqualTo(1011);
	}
	
	@Test
	public void deleteWithNoLogIn() {
		HttpHeaders de_head = new HttpHeaders();
		de_head.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		de_head.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> de_venue = new HttpEntity<String>("{ \"name\": \"Venue\" }", de_head);
		
		ResponseEntity<String> results = bad_example.exchange(base_url, HttpMethod.DELETE, de_venue, String.class);

		assertThat(results.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void postVenueWithNoLogIn() {
		HttpHeaders post_head = new HttpHeaders();
		post_head.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		post_head.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> post_venue = new HttpEntity<String>("{ \"name\": \"NEW_VENUE\" }", post_head);

		ResponseEntity<String> results = bad_example.exchange(base_url, HttpMethod.POST, post_venue, String.class);

		assertThat(results.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
	}
	
	
	
	
	
}
