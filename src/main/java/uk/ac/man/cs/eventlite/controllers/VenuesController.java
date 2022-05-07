package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		if(venue.checkAll(venue) == "All pass") {
			model.addAttribute("error", "");
			venueService.save(venue);
			return "redirect:/events";
		}else {
//			String errorMsg = venue.getErrorMsg();
//			model.addAttribute("error", errorMsg);
			return "venues/addVenue";

//			venueService.save(venue);
//			redirectAttrs.addFlashAttribute("ok_message", "Venue added");

		//return "redirect:/venues";
	}
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String getSearchedVenue(Model model, @RequestParam (value = "search", required = true) String name) {
		model.addAttribute("venues", venueService.listVenueByNameIgnoreCase(name));
		return "venues/index";
	}
	
	
	@PostMapping("/edit/{id}")
	public String updateById(Model model, @PathVariable("id") long id, @ModelAttribute Venue venue_in) {
		model.addAttribute("venue_new", venue_in);
		
		Venue venueToEdit = venueService.findById(id).get();
		if (venue_in.getName()!= null && venue_in.getName().length() <=256) {
			venueToEdit.setName(venue_in.getName());
		}
		if (venue_in.getAddress() != null&&venue_in.getAddress().length() <=500) {
			venueToEdit.setAddress(venue_in.getAddress());
		}
		if (venue_in.getPostcode() != null&&venue_in.getPostcode().length() <=100) {
			venueToEdit.setPostcode(venue_in.getPostcode());
		}
		

		if(venue_in.checkCapacity(venue_in)) {
			venueToEdit.setCapacity(venue_in.getCapacity());
		}
		
	
		venueService.save(venueToEdit);

		return "redirect:/venues";
	}
	

}