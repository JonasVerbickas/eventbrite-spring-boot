package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesController.class)
@Import(Security.class)
class VenuesControllerTest {
 
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

}