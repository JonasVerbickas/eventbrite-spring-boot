package uk.ac.man.cs.eventlite.controllers;

<<<<<<< HEAD
import java.util.ArrayList;
=======
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
>>>>>>> origin/venueapi_test

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
<<<<<<< HEAD
	@ExceptionHandler(VenueNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	
	public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "venues/not_found";
	}

	
=======
>>>>>>> origin/venueapi_test
	@GetMapping
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAllByOrderByNameAsc());

		return "venues/index";
	}
	
	@GetMapping("/{id}") 
	public String event(@PathVariable("id") long id, Model model) throws Exception {

		Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));

		Collection<Event> collection = new ArrayList<Event>();
		
		int counter = 0;
		
		for (Event e: eventService.findAllByVenueOrderByDateAscNameAsc(venue)) {
			
			if (e.getDate().compareTo(LocalDate.now())>=0) {
				collection.add(e);
				counter = counter + 1;
			}
			
			if (counter == 3) {
				break;
			}
			
		}
		
		model.addAttribute("events3", collection);

		model.addAttribute("v", venue);

		return "venues/venue_detail";
	}
	
	@GetMapping("/edit/{id}")
	public String forDropDownVenuesinEdit(Model model, @PathVariable("id") long id) {
		Venue venue = venueService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

		model.addAttribute("venue_old", venue);
		model.addAttribute("venue_new", new Venue());
		model.addAttribute("venues", venueService.findAll());

		return "venues/editVenue";
	}
	
	@DeleteMapping("/{id}/delete")
	public String deleteById(@PathVariable("id") long id)
	{
		for(Event e: eventService.findAll()) {
			if(e.getVenue().getId() == id) {
				return "redirect:/venues/"+id;
			}
			
		}
		venueService.deleteById(id);
		return "redirect:/venues";
	}
	
	@PutMapping("/{id}/delete/address")
	public String deleteTime(@PathVariable("id") long id) {
		Venue v = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
		v.setAddress(null);
		venueService.save(v);
		return "redirect:/venues/"+id;
	}

	@PutMapping("/{id}/delete/capacity")
	public String deleteDate(@PathVariable("id") long id) {
		Venue v = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
		v.setCapacity(0);
		venueService.save(v);
		return "redirect:/venues/"+id;
	}
	
	@PutMapping("/{id}/delete/postcode")
	public String deleteDate2(@PathVariable("id") long id) {
		Venue v = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
		v.setPostcode(null);
		venueService.save(v);
		return "redirect:/venues/"+id;
	}

	@PutMapping("/{id}/delete/all_fields")
	public String deleteAllFields(@PathVariable("id") long id) {
		Venue v = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
//		if()
		v.setCapacity(0);
		v.setName(null);
		v.setAddress(null);
		v.setPostcode(null);
		// this does not work e.setVenue(null);
		venueService.save(v);
		return "redirect:/venues/"+id;
	}
	
	@GetMapping("/addVenue")
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/addVenue";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("venue");
			return "venues/addVenue";
		}
	
			venueService.save(venue);
			return "redirect:/venues";
		

//			venueService.save(venue);
//			redirectAttrs.addFlashAttribute("ok_message", "Venue added");

		//return "redirect:/venues";
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String getSearchedVenue(Model model, @RequestParam (value = "search", required = true) String search_value) {
		model.addAttribute("search_value", search_value);
		model.addAttribute("venues", venueService.listVenueByNameIgnoreCase(search_value));
		return "venues/index";
	}
	
	
	@PostMapping("/edit/{id}")
	public String updateById(Model model, @PathVariable("id") long id, @Valid @ModelAttribute("venue_old") Venue venue_in,  BindingResult errors, 
			RedirectAttributes redirectAttrs) {
		
		if (errors.hasErrors()) {
			model.addAttribute("venue_old", venue_in);
			return "venues/editVenue";
		}
		
		Venue venueToEdit = venueService.findById(id).get();
			venueToEdit.setName(venue_in.getName());
		
			venueToEdit.setAddress(venue_in.getAddress());
		
		
			venueToEdit.setPostcode(venue_in.getPostcode());
		
		

			venueToEdit.setCapacity(venue_in.getCapacity());
		
		
	
		venueService.save(venueToEdit);

		return "redirect:/venues";
	}
	

}