package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.core.StringContains.containsString;

import java.util.Collections;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.man.cs.eventlite.config.data.InitialDataLoader;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import org.springframework.http.ResponseEntity;
import uk.ac.man.cs.eventlite.entities.Event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenuesControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private WebTestClient client;
	@Autowired
	private VenueService venueService;

	@Autowired
	private EventService eventService;

	private int currentRows;

	@BeforeEach
	public void setup() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void deleteVenueNoLogin() {
		long id = eventService.findAll().iterator().next().getId();
		client.delete().uri("/venue/" + id).accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));
	}

@Test
	public void getEditNoLogIn() {
		long id = venueService.findAll().iterator().next().getId();
		client.get().uri("/venues/edit/"+id).accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));
	}

	@Test
	public void getNewNoLogIn() {
		client.get().uri("/venues/addVenue").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));

			}


	@Test
	public void getAllVenuesPage() {
		client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class)
				.consumeWith(result -> {
					for(Venue v: venueService.findAll())
					{
						assertThat(result.getResponseBody(), containsString(v.getName()));
						assertThat(result.getResponseBody(), containsString(""+v.getCapacity()));
						assertThat(result.getResponseBody(), containsString(v.getAddress()));
					}
				});
	}

	@Test
	public void getVenuePage() {
		Venue v = venueService.findAll().iterator().next();
		client.get().uri("/venues/"+v.getId()).accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString(v.getName()));
					assertThat(result.getResponseBody(), containsString(""+v.getCapacity()));
					assertThat(result.getResponseBody(), containsString(v.getAddress()));
					assertThat(result.getResponseBody(), containsString(v.getPostcode()));
				});
	}

}