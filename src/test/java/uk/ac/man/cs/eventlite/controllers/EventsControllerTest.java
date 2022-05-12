package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
import uk.ac.man.cs.eventlite.dao.TwitterServiceImpl;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

import javax.swing.text.html.Option;

@ContextConfiguration(classes = {EventsController.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

	@Autowired
	private EventsController eventsController;

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

	@MockBean
	private TwitterServiceImpl twitterServiceImpl;

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAllByOrderByDateAscNameAsc()).thenReturn(Collections.<Event>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAllByOrderByDateAscNameAsc();
		verifyNoInteractions(event);
		verifyNoInteractions(venue);
	}


	@Test
	void testDeleteByIdSuccess() throws Exception {
		doNothing().when(this.eventService).deleteById(anyLong());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/events/{id}/delete", 42L);
		MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/events"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events"));
		verify(eventService, times(1)).deleteById(anyLong());
	}

	@Test
	void testDeleteByIdEventNotFound() throws Exception {
		doThrow(new EventNotFoundException(42L)).when(this.eventService).deleteById(anyLong());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/events/{id}/delete", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("events/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"));
		verify(eventService, times(1)).deleteById(anyLong());
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		// JavaScript map crashes with mocked Event/Venues
		Event e = new Event();
		Venue v = new Venue();
		e.setVenue(v);
		when(eventService.findAllByOrderByDateAscNameAsc()).thenReturn(Collections.<Event>singletonList(e));
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));
		verify(eventService).findAllByOrderByDateAscNameAsc();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
		verify(eventService, times(1)).findById(99L);
	}

	@Test
	void testDeleteDateSuccess() throws Exception {
		when(this.eventService.save((Event) any())).thenReturn(event);
		when(this.eventService.findById(anyLong())).thenReturn(Optional.ofNullable(event));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/events/{id}/delete/date", 42L);
		MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/events/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events/42"));
		verify(event, times(1)).setDate(null);
		verify(eventService, times(1)).save(event);
	}

	@Test
	void testDeleteDateEventNotFound() throws Exception {
		when(this.eventService.save((Event) any())).thenThrow(new EventNotFoundException(42L));
		when(this.eventService.findById(anyLong())).thenReturn(Optional.ofNullable(event));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/events/{id}/delete/date", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("events/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"));
		verify(event, times(1)).setDate(null);
		verify(eventService, times(1)).save(event);
	}


	@Test
	void testDeleteTimeSuccess() throws Exception {

		when(this.eventService.save((Event) any())).thenReturn(event);
		when(this.eventService.findById(anyLong())).thenReturn(Optional.ofNullable(event));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/events/{id}/delete/time", 42L);
		MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/events/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events/42"));
		verify(event, times(1)).setTime(null);
		verify(eventService, times(1)).save(event);
	}

	@Test
	void testDeleteTimeEventNotFound() throws Exception {
		when(this.eventService.save((Event) any())).thenThrow(new EventNotFoundException(42L));
		when(this.eventService.findById(anyLong())).thenReturn(Optional.ofNullable(event));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/events/{id}/delete/time", 42L);
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.model().size(1))
				.andExpect(MockMvcResultMatchers.model().attributeExists("not_found_id"))
				.andExpect(MockMvcResultMatchers.view().name("events/not_found"))
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"));
		verify(event, times(1)).setTime(null);
		verify(eventService, times(1)).save(event);
	}
}
