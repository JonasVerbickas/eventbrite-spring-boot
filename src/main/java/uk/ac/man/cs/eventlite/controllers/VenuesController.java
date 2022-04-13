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

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private VenueService venueService;
	
	@GetMapping
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAllByOrderByNameAsc());

		return "venues/index";
	}
	
	@GetMapping("/{id}") 
	public String event(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) throws Exception {

		Venue venue = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));


		model.addAttribute("v", venue);

		return "venues/venue_detail";
	}
	
	@GetMapping("/edit/{id}")
	public String newGreeting(Model model) {
		return "venues/edit";
	}
	
	@DeleteMapping("/{id}/delete")
	public String deleteById(@PathVariable("id") long id)
	{
		venueService.deleteById(id);
		return "redirect:/venues";
	}
	
	@PutMapping("/{id}/delete/address")
	public String deleteTime(@PathVariable("id") long id) {
		Venue v= venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
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

	@PutMapping("/{id}/delete/all_fields")
	public String deleteAllFields(@PathVariable("id") long id) {
		Venue v = venueService.findById(id).orElseThrow(() -> new VenueNotFoundException(id));;
		v.setCapacity(0);
		v.setName(null);
		v.setAddress(null);
		// this does not work e.setVenue(null);
		venueService.save(v);
		return "redirect:/venues/"+id;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String getSearchedVenue(Model model, @RequestParam (value = "search", required = true) String name) {
		model.addAttribute("venues", venueService.listVenueByNameIgnoreCase(name));
		return "venues/index";
	}
	

}