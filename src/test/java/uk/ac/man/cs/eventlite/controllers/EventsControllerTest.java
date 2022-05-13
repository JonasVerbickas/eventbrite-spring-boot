package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify; 
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;




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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.equalTo;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterServiceImpl;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

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
	}
	
	@Test
	public void insertEventSuccessfulCheck() throws Exception{
		ArgumentCaptor <Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);
		
		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
		.with(user("Tom").roles(Security.ADMIN_ROLE))
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.param("id", "1")
		.param("name", "MockName")
		.param("venue.id", "2")
		.param("date", "2023-10-20")
		.param("time", "12:30")
		.param("description", "Boringg")
		.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(view().name("redirect:/events"))
		.andExpect(handler().methodName("createEvent"));
	verify(eventService).save(arg.capture());
	assertThat("MockName", equalTo(arg.getValue().getName()));
	assertThat(1L, equalTo(arg.getValue().getId()));
	assertThat(2L, equalTo(arg.getValue().getVenue().getId()));
	}
	
	@Test
	public void editEventSucessfulCheck() throws Exception {
		
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		Event e = new Event();
		e.setId(1);
		e.setName("MockName");
		e.setVenue(venue);
		when(eventService.findById(1L)).thenReturn(Optional.of(e));
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		
		mvc.perform(post("/events/edit/1").with(user("Tom").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "1")
				.param("name", "MockNameUpdated")
				.param("venue.id","2")
				.param("date", "9999-10-23")
				.param("time", "12:50")
				.param("description", "AAA")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/events"))
				.andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("updateById"));
		verify(eventService).save(arg.capture());
		assertThat("MockNameUpdated", equalTo(arg.getValue().getName()));
		assertThat(2L, equalTo(arg.getValue().getVenue().getId()));
		assertThat(1L, equalTo(arg.getValue().getId()));
		
		
	}
	
	
}
