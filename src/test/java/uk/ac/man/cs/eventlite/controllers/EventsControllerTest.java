package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;
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
import org.mockito.ArgumentCaptor;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.equalTo;

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
	void testDeleteAllFieldsSuccess() throws Exception {
		when(this.eventService.save((Event) org.mockito.Mockito.any())).thenReturn(event);
		when(this.eventService.findById(anyLong())).thenReturn(Optional.ofNullable(event));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/events/{id}/delete/all_fields", 42L);
		MockMvcBuilders.standaloneSetup(this.eventsController)
				.build()
				.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.view().name("redirect:/events/42"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events/42"))
				.andExpect(handler().methodName("deleteAllFields"));
		verify(event, times(1)).setTime(null);
		verify(event, times(1)).setDate(null);
		verify(event, times(1)).setDescription(null);
		verify(eventService, times(1)).save(event);
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
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events"))
				.andExpect(handler().methodName("deleteById"));
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
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"))
				.andExpect(handler().methodName("deleteById"));
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
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events/42"))
				.andExpect(handler().methodName("deleteDate"));
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
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"))
				.andExpect(handler().methodName("deleteDate"));
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
				.andExpect(MockMvcResultMatchers.redirectedUrl("/events/42"))
				.andExpect(handler().methodName("deleteTime"));
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
				.andExpect(MockMvcResultMatchers.forwardedUrl("events/not_found"))
				.andExpect(handler().methodName("deleteTime"));
		verify(event, times(1)).setTime(null);
		verify(eventService, times(1)).save(event);
	}

	@Test
	public void insertEventSuccessfulCheck() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
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
	public void testAddNoNameEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "")
						.param("venue.id", "2")
						.param("date", "2023-10-20")
						.param("time", "12:30")
						.param("description", "Boringg")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
	@Test
	public void testAddIllegalNameEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						//the length of the name is 290
						.param("name", "Illegallllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll")
						.param("venue.id", "2")
						.param("date", "2023-10-20")
						.param("time", "12:30")
						.param("description", "Boringg")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
	@Test
	public void testAddNoVenueEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "TestAddNoVenue")
						.param("venue.id", "")
						.param("date", "2023-10-20")
						.param("time", "12:30")
						.param("description", "Boringg")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
	@Test
	public void testAddNoDesEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "TestAddNoVenue")
						.param("venue.id", "2")
						.param("date", "2023-10-20")
						.param("time", "12:30")
						.param("description", "")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
	@Test
	public void testAddIllegalDesEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "TestAddNoVenue")
						.param("venue.id", "2")
						.param("date", "2023-10-20")
						.param("time", "12:30")
						//the lengh of the description is 1054
						.param("description", "illegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllll")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
	@Test
	public void testAddNoDateEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "TestAddNoVenue")
						.param("venue.id", "2")
						.param("date", "")
						.param("time", "12:30")
						.param("description", "Boringg")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
	}
	
//	@Test
//	public void testAddNoTimeEvent() throws Exception{
//		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
//		when(eventService.save(any(Event.class))).then(returnsFirstArg());
//		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
//		when(venue.getId()).thenReturn(2L);
//
//		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
//						.with(user("Tom").roles(Security.ADMIN_ROLE))
//						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//						.param("id", "1")
//						.param("name", "TestAddNoVenue")
//						.param("venue.id", "2")
//						.param("date", "2023-10-20")
//						.param("time", "")
//						.param("description", "Borrrrrrrrrrrrring")
//						.accept(MediaType.TEXT_HTML).with(csrf()))
//				.andExpect(view().name("events/new"));
//		
//	}
		
	
	
	@Test
	public void testAddPastDateEvent() throws Exception{
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		when(eventService.save(any(Event.class))).then(returnsFirstArg());
		when(venueService.findById(2L)).thenReturn(Optional.of(venue));
		when(venue.getId()).thenReturn(2L);

		mvc.perform(post("/events").accept(MediaType.TEXT_HTML)
						.with(user("Tom").roles(Security.ADMIN_ROLE))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", "1")
						.param("name", "TestAddNoVenue")
						.param("venue.id", "2")
						.param("date", "2020-10-20")
						.param("time", "12:30")
						.param("description", "Borrrrrrrrrrrrring")
						.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/new"));
		
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
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", "Very Boringggg")
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
	

	@Test 
	public void getEventByNameSearchTest() throws Exception{ 
		Event eventByname = new Event(); 
		Venue venue_local = new Venue();
		eventByname.setName("MockName"); 
		eventByname.setVenue(venue_local); 
		eventByname.setDescription("Come onn");
		eventByname.setId(1);
		eventByname.setDate(LocalDate.of(2023, 2, 12));
		eventByname.setTime(LocalTime.of(15, 1, 15));
		when(eventService.findByNameContainingIgnoreCaseOrderByDateAscNameAsc("MockName")).thenReturn(Collections.<Event>singletonList(eventByname)); 
		
		mvc.perform(get("/events/search?search=MockName") 
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().is2xxSuccessful())		
				.andExpect(view().name("events/index"))
				.andExpect(handler().methodName("getSearchedEvent")); 
		verify(eventService).findByNameContainingIgnoreCaseOrderByDateAscNameAsc("MockName"); 
		}

	@Test
	public void editEventSucesswithNoName() throws Exception {
		
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
				.param("name", " ")
				.param("venue.id","2")
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", "Very Boringggg")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithBadName() throws Exception {
		
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
				.param("name", "Illegallllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll")
				.param("venue.id","2")
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", "Very Boringggg")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithNoVenue() throws Exception {
		
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
				.param("name", "TestName")
				.param("venue.id","")
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", "Very Boringggg")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithNoDes() throws Exception {
		
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
				.param("name", "TestName")
				.param("venue.id","2")
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", " ")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithBadDes() throws Exception {
		
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
				.param("name", "TestName")
				.param("venue.id","2")
				.param("date", "2023-10-23")
				.param("time", "12:34")
				.param("description", "illegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllllillegallllllllllllllllllllllllllll")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithNoDate() throws Exception {
		
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
				.param("name", "TestName")
				.param("venue.id","2")
				.param("date", "")
				.param("time", "12:34")
				.param("description", "Boring stuff")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	
	@Test
	public void editEventSucesswithPastDate() throws Exception {
		
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
				.param("name", "TestName")
				.param("venue.id","2")
				.param("date", "2020-10-23")
				.param("time", "12:34")
				.param("description", "Boring stuff")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(view().name("events/edit"));
		
	}
	

	

}
