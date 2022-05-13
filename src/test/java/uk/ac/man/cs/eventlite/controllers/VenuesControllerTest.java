package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

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
	public void getVenueNotFound() throws Exception {
		mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("venues/not_found"));
		verify(venueService, times(1)).findById(99L);
	}
	
	@Test
	public void getIndexWithVenues() throws Exception {


		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));
		
		verify(venueService).findAllByOrderByNameAsc();
	}
	
	@Test
	public void getDetailedVenuePage() throws Exception {
		when(venueService.findById((long) 1)).thenReturn(Optional.of(venue));

		mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML))
			.andExpect(status().isOk())
			.andExpect(view().name("venues/venue_detail")).andExpect(handler().methodName("event"));

		verify(venueService).findById((long) 1);
	}
	
	
	@Test
	void testDeleteByIdSuccess() throws Exception {
		doNothing().when(this.venueService).deleteById(anyLong());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/venues/{id}/delete", 42L);
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/venues"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/venues"));
		verify(venueService, times(1)).deleteById(anyLong());
	}
	
	@Test
	void testDeleteByIdVenueNotFound() throws Exception {
		doThrow(new VenueNotFoundException(42L)).when(this.venueService).deleteById(anyLong());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/venues/{id}/delete", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("venues/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/not_found"));
		verify(venueService, times(1)).deleteById(anyLong());
	}
	
	@Test
	void testDeleteAddressSuccess() throws Exception {
		when(this.venueService.save((Venue) any())).thenReturn(venue);
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/address", 42L);
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/venues/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/venues/42"));
		verify(venue, times(1)).setAddress(null);
		verify(venueService, times(1)).save(venue);
	}
	
	@Test
	void testDeleteAddressVenueNotFound() throws Exception {
		when(this.venueService.save((Venue) any())).thenThrow(new VenueNotFoundException(42L));
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/address", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("venues/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/not_found"));
		verify(venue, times(1)).setAddress(null);
		verify(venueService, times(1)).save(venue);
	}
	
	@Test
	void testDeleteCapacitySuccess() throws Exception {
		when(this.venueService.save((Venue) any())).thenReturn(venue);
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/capacity", 42L);
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/venues/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/venues/42"));
		verify(venue, times(1)).setCapacity(0);
		verify(venueService, times(1)).save(venue);
	}
	
	
	@Test
	void testDeleteCapacityVenueNotFound() throws Exception {
		when(this.venueService.save((Venue) any())).thenThrow(new VenueNotFoundException(42L));
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/capacity", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("venues/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/not_found"));
		verify(venue, times(1)).setCapacity(0);
		verify(venueService, times(1)).save(venue);
	}
	
	@Test
	void testDeletePostcodeSuccess() throws Exception {
		when(this.venueService.save((Venue) any())).thenReturn(venue);
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/postcode", 42L);
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/venues/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/venues/42"));
		verify(venue, times(1)).setPostcode(null);
		verify(venueService, times(1)).save(venue);
	}
	
	@Test
	void testDeletePostcodeVenueNotFound() throws Exception {
		when(this.venueService.save((Venue) any())).thenThrow(new VenueNotFoundException(42L));
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/postcode", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("venues/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("venues/not_found"));
		verify(venue, times(1)).setPostcode(null);
		verify(venueService, times(1)).save(venue);
	}
	
	
	
	@Test
	void testDeleteAllfieldsSuccess() throws Exception {
		when(this.venueService.save((Venue) any())).thenReturn(venue);
		when(this.venueService.findById(anyLong())).thenReturn(Optional.ofNullable(venue));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/venues/{id}/delete/all_fields", 42L);
		MockMvcBuilders.standaloneSetup(this.venuesController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/venues/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/venues/42"));
		
		verify(venue, times(1)).setAddress(null);
		verify(venue, times(1)).setCapacity(0);
		verify(venue, times(1)).setPostcode(null);
		verify(venueService, times(1)).save(venue);
	}
	
	@Test
	public void insertVenueSuccessfulCheck() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "MockName")
		
		.param("capacity", "200")
		.param("address", "MockAddress")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("redirect:/venues"))
		.andExpect(handler().methodName("createVenue"));
	verify(venueService).save(arg.capture());
	assertThat("MockName", equalTo(arg.getValue().getName()));
	assertThat(1L, equalTo(arg.getValue().getId()));
	}
	
	@Test
	public void insertVenueWithNoName() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "")
		
		.param("capacity", "200")
		.param("address", "MockAddress")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	@Test
	public void insertVenueWithIllegalName() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "Illegallllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll")
	
		.param("capacity", "200")
		.param("address", "MockAddress")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	@Test
	public void insertVenueWithbadCapacity() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "test name")
	
		.param("capacity", "-1")
		.param("address", "MockAddress")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	@Test
	public void insertVenueWithNoaddress() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "normal")
	
		.param("capacity", "200")
		.param("address", "")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	@Test
	public void insertVenueWithBadaddress() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "normal")
	
		.param("capacity", "100")
		.param("address", "illegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllll")
		.param("postcode", "M1 7JQ")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	@Test
	public void insertVenueWithNoZip() throws Exception{
		ArgumentCaptor <Venue> arg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.save(any(Venue.class))).then(returnsFirstArg());
		
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/venues").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "normal")
	
		.param("capacity", "100")
		.param("address", "testAddress")
		.param("postcode", "")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("venues/addVenue"));
	}
	
	
	
	@Test
	public void createVenueWithoutCSRFTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("tom").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().isForbidden());
		verify(venueService, never()).save(venue);
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
