package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Tweet;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterServiceImpl;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private TwitterServiceImpl twitterService;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}

	@GetMapping
	public String getAllEvents(Model model) throws TwitterException {
		model.addAttribute("events", eventService.findAllByOrderByDateAscTimeAsc());
		List<Status> timeline = twitterService.getTimeline();
		model.addAttribute("timeline", timeline);
		return "events/index";
	}


	// `prev_tweet_text` is used to display a text of the tweet made by the current user for this particular event
	@GetMapping("/{id}")
	public String event(@PathVariable("id") long id, Model model, @ModelAttribute("prev_tweet_text") String prev_tweet_text)  {

		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

		model.addAttribute("event", event);
		System.out.println("prev_tweet_text" + prev_tweet_text);
		model.addAttribute("tweet", new Tweet());
		
		return "events/event_detail";
	}

	@GetMapping("/edit/{id}")
	public String updatePageRedirect(Model model, @PathVariable("id") long id) {
		Event event = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));

		model.addAttribute("id", event.getId());
		model.addAttribute("event_new", new Event());

		return "events/edit";
	}

	@DeleteMapping("/{id}/delete")
	public String deleteById(@PathVariable("id") long id) {
		eventService.deleteById(id);
		return "redirect:/events";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String getSearchedEvent(Model model, @RequestParam(value = "search", required = true) String name) {
		model.addAttribute("events", eventService.listEventByNameIgnoreCase(name));
		return "events/index";
	}

	@PutMapping("/{id}/delete/time")
	public String deleteTime(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		;
		e.setTime(null);
		eventService.save(e);
		return "redirect:/events/" + id;
	}

	@PutMapping("/{id}/delete/date")
	public String deleteDate(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		;
		e.setDate(null);
		eventService.save(e);
		return "redirect:/events/" + id;
	}

	@PutMapping("/{id}/delete/all_fields")
	public String deleteAllFields(@PathVariable("id") long id) {
		Event e = eventService.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		e.setDate(null);
		e.setName(null);
		e.setTime(null);
		// this does not work e.setVenue(null);
		eventService.save(e);
		return "redirect:/events/" + id;
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

	@PostMapping("/edit/{id}")
	public String updateById(Model model, @PathVariable("id") long id, @ModelAttribute Event event_in) {
		model.addAttribute("event_new", event_in);
		Event eventToEdit = eventService.findById(id).get();
		eventToEdit.setName(event_in.getName());
		if (event_in.getDate() != null) {
			eventToEdit.setDate(event_in.getDate());
		}

		if (event_in.getTime() != null) {
			eventToEdit.setTime(event_in.getTime());
		}

		if (event_in.getVenue() != null) {

			eventToEdit.setVenue(event_in.getVenue());
		}

		eventService.save(eventToEdit);

		return "redirect:/events";
	}

	@PostMapping("/{id}/post_tweet")
	public ModelAndView postTweet(Model model, @PathVariable("id") long id, @ModelAttribute Tweet tweet) throws TwitterException {
		twitterService.postATweet(tweet.getTweetText());
		ModelMap modelMap = new ModelMap("prev_tweet_text", tweet.getTweetText());
		return new ModelAndView("redirect:/events/" + id, modelMap);
	}

	@GetMapping("/get_timeline")
	public String getTimeline() throws TwitterException {
		twitterService.getTimeline();
		return "redirect:/events" ;
	}
}
