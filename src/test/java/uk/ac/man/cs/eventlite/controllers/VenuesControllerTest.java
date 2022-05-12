package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@ContextConfiguration(classes = {VenuesController.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesController.class)
@Import(Security.class)
class VenuesControllerTest {

	@Autowired
	private VenuesController venuesController;

	@Autowired
	private MockMvc mvc;

	@Mock
	private Venue venue;

	@MockBean
	private VenueService venueService;

	@MockBean
	private EventService eventService;


// @Test
// public void getEventNotFound() throws Exception {
//  mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
//    .andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenues"));
// }

	@Test
	public void newVenue() throws Exception {
		mvc.perform(get("/venues/addVenue").with(user("Caroline").roles(Security.ADMIN_ROLE))
						.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("venues/addVenue")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("newVenue"));
	}


	@Test
	public void addVenueTest() throws Exception {
		ArgumentCaptor<Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());

		mvc.perform(post("/venues").with(user("Caroline").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "test")
						.param("capacity", "200")
						.param("address", "my house")
						.param("postcode", "M1 5PS")
						.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors());
		verify(venueService).save(arg.capture());
		assertThat("test", equalTo(arg.getValue().getName()));
	}


	@Test
	public void editVenue() throws Exception {
		when(venueService.findById(1)).thenReturn(Optional.of(venue));
		mvc.perform(get("/venues/edit/1").with(user("Rob").roles(Security.ADMIN_ROLE))
						.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("venues/editVenue")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("forDropDownVenuesinEdit"));
	}

	@Test
	@WithMockUser(username = "Caroline", roles = {"ADMINISTRATOR"})
	public void capacityUpdateErrorTest() throws Exception {
		when(venueService.findById(0)).thenReturn(null);

		mvc.perform(MockMvcRequestBuilders.patch("/venues/0").accept(MediaType.TEXT_HTML).with(csrf())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("id", "0")
						.param("name", "test1")
						.param("address", "test2")
						.param("postcode", "test3")
						.sessionAttr("venue", venue)
						.param("description", "test"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	@WithMockUser(username = "Caroline", roles = {"ADMINISTRATOR"})
	public void capacityNumberErrorTest() throws Exception {
		when(venueService.findById(0)).thenReturn(null);

		mvc.perform(MockMvcRequestBuilders.patch("/venues/0").accept(MediaType.TEXT_HTML).with(csrf())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.param("id", "0")
						.param("name", "test1")
						.param("address", "test2")
						.param("postcode", "test3")
						.param("capacity", "-1")
						.sessionAttr("venue", venue)
						.param("description", "test"))
				.andExpect(status().isMethodNotAllowed());
	}


	@Test
	public void deleteVenueTest() throws Exception {

		mvc.perform(delete("/venues/1").with(user("Caroline").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML)
				.with(csrf())).andExpect(status().isMethodNotAllowed());
	}

	/**
	 * Method under test: {@link VenuesController#getAllVenues(org.springframework.ui.Model)}
	 */
	@Test
	void testGetAllVenues() throws Exception {
		when(this.venueService.findAllByOrderByNameAsc()).thenReturn((Iterable<Venue>) mock(Iterable.class));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/venues");
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().size(1))
				.andExpect(MockMvcResultMatchers.model().attributeExists("venues"))
				.andExpect(MockMvcResultMatchers.view().name("venues/index"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/index"));
	}

	/**
	 * Method under test: {@link VenuesController#getAllVenues(org.springframework.ui.Model)}
	 */
	@Test
	void testGetAllVenues2() throws Exception {
		when(this.venueService.findAllByOrderByNameAsc()).thenReturn((Iterable<Venue>) mock(Iterable.class));
		MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/venues");
		getResult.contentType("https://example.org/example");
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(getResult)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().size(1))
				.andExpect(MockMvcResultMatchers.model().attributeExists("venues"))
				.andExpect(MockMvcResultMatchers.view().name("venues/index"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/index"));
	}


}