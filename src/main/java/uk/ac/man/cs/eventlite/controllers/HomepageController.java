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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;


@Controller
@RequestMapping(value = "", produces = { MediaType.TEXT_HTML_VALUE })
public class HomepageController{
	
	@Autowired
	private EventService eventService;
	private VenueService venueService;
	
	
	
	@GetMapping
	public String getAllEvents(Model model) {
		
		Collection<Event> collection = new ArrayList<Event>();
		Collection<Venue> venueCollection = new ArrayList<Venue>();
		Map<Venue, Integer> dic = new HashMap<Venue, Integer>();
		
		int counter = 0;
		
		Venue venue;
		
		int curEvents = 0;
		
		for(Event e: eventService.findAllByOrderByDateAscTimeAsc()) {
			
			if (e.getDate().compareTo(LocalDate.now())>=0) {
				collection.add(e);
				counter = counter + 1;
			}
			
			if (counter == 3) {
				break;
			}
		}
		
		for(Event e: eventService.findAllByOrderByDateAscTimeAsc()) {
			
			venue = e.getVenue();
			
			if (!dic.containsKey(venue)) {
				dic.put(venue, 0);
			} else {
				curEvents = dic.get(venue);
				curEvents++;
				dic.replace(venue, curEvents);
			}
			
		}
		
		Map<Venue, Integer> sortedMapInDescending = dic.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
						(entry1, entry2) -> entry2, LinkedHashMap::new));
		
		
		counter = 0;
		
		for (Venue vv: sortedMapInDescending.keySet()) {
			venueCollection.add(vv);
		}
		model.addAttribute("events", collection);
		model.addAttribute("venues", venueCollection);
		
		return "home/homepage";
	}
}
