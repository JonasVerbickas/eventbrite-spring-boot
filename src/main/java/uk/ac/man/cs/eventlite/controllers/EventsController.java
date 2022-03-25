package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;


@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;
	private VenueService venueService;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}

//	@GetMapping("/{id}")
//	public String getEvent(@PathVariable("id") long id, Model model) {
//		throw new EventNotFoundException(id);
//	}

	@GetMapping
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAllByOrderByDateAscTimeAsc());

		return "events/index";
	}
	
	@GetMapping("/{id}") 
	public String event(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));


		model.addAttribute("event", event);

		return "events/show";
	}



	@GetMapping("/edit/{id}")
	public String newGreeting(Model model) {
//		if (!model.containsAttribute("greeting")) {
//			model.addAttribute("greeting", new Event());
//		}

		return "events/edit";
	}
	
	@DeleteMapping("/{id}/delete")
	public String deleteById(@PathVariable("id") long id)
	{
		eventService.deleteById(id);
		return "redirect:/events";
	}
	
	@PutMapping("/{id}/delete/time")
	public String deleteTime(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));;
		e.setTime(null);
		eventService.save(e);
		return "redirect:/events/"+id;
	}

	@PutMapping("/{id}/delete/date")
	public String deleteDate(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));;
		e.setDate(null);
		eventService.save(e);
		return "redirect:/events/"+id;
	}

	@PutMapping("/{id}/delete/all_fields")
	public String deleteAllFields(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));;
		e.setDate(null);
		e.setName(null);
		e.setTime(null);
		// this does not work e.setVenue(null);
		eventService.save(e);
		return "redirect:/events/"+id;
	}
	
	@GetMapping("/new")
	public String newEvent(Model model) {
		  if (!model.containsAttribute("event")) {
		   model.addAttribute("event", new Event());
		  }
		  return "events/new";
	 }
	
	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			return "events/new";
		}
		
		event.setDate(LocalDate.parse("2012-02-11"));
		event.setTime(LocalTime.parse("12:20:20"));
		Venue v1 = new Venue();
		v1.setId(1);
		event.setVenue(v1);
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}



}
 