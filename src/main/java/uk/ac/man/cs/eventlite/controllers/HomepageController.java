package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;
import java.util.Collection;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping(value = "/home", produces = { MediaType.TEXT_HTML_VALUE })
public class HomepageController{
	
	@Autowired
	private EventService eventService;
	private VenueService venueService;
	
	@GetMapping
	public String getAllEvents(Model model) {
		
		Collection<Event> collection = new ArrayList<Event>();
		int counter = 0;
		
		for(Event e: eventService.findAllByOrderByDateAscTimeAsc()) {
			
			if (e.getDate().compareTo(LocalDate.now())>=0) {
				collection.add(e);
				counter = counter + 1;
			}
			
			if (counter == 3) {
				break;
			}
		}

		model.addAttribute("events", collection);
		
		return "home/homepage";
	}
}
